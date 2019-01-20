package com.power.wechatpush.job;

import com.power.wechatpush.video.VideoSDK;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.*;

@Service
public class TaskExecutorService {

    private static final Logger LOG = LoggerFactory.getLogger(TaskExecutorService.class);

    @Value("${video.address}")
    private String address;

    @Value("${video.username}")
    private String username;

    @Value("${video.password}")
    private String password;

    @Value("${video.epid}")
    private String epid;

    private long[] sesson;

    @PostConstruct
    public void init() {
        int ret = VideoSDK.initialize();
        LOG.info("初始化完成, ret={}\n", ret);
        sesson = new long[1];
        ret = VideoSDK.open(address, username, password, epid, sesson);
        LOG.info("已打开连接, ret={}\n", ret);

        VideoSDK.vadrGlobalInit();
        LOG.info("VADR GlobalInit 完成");
    }

    @PreDestroy
    public void destroy() {
        VideoSDK.vardrGlobalClose();

        int ret = VideoSDK.close(sesson[0]);
        LOG.info("已关闭连接, ret={}\n", ret);

        ret = VideoSDK.terminate();
        LOG.info("已终止, ret={}\n", ret);

    }

    private ThreadPoolExecutor threadPoolExecutor =  new ThreadPoolExecutor(1, 1,
            0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());

    public <T> Future<T> submit(Callable<T> task) {
        return threadPoolExecutor.submit(task);
    }


    public long getSesson() {
        return sesson[0];
    }

}
