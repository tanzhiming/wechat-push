package com.power.wechatpush.shiro;

import com.power.wechatpush.dao.StaffDao;
import com.power.wechatpush.dao.entity.Staff;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

public class MyShiroRealm extends AuthorizingRealm {

    @Resource
    private StaffDao staffDao;

    //授权
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        Staff user= (Staff) SecurityUtils.getSubject().getPrincipal();//User{id=1, username='admin', password='3ef7164d1f6167cb9f2658c07d3c2f0a', enable=1}
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("userid",user.getId());
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        return info;
    }

    //认证
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        //获取用户的输入的账号.
        String username = (String)token.getPrincipal();
        Staff staff = staffDao.findStaffByName(username);
        if(staff==null) throw new UnknownAccountException();
        if (Staff.STATUS_FORBID == staff.getStatus()) {
            throw new LockedAccountException(); // 帐号锁定
        }
        SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(
                staff, //用户
                staff.getPassword(), //密码
                null,
                getName()  //realm name
        );
        // 当验证都通过后，把用户信息放在session里
        Session session = SecurityUtils.getSubject().getSession();
        session.setAttribute("userSession", staff);
        session.setAttribute("userSessionId", staff.getId());
        return authenticationInfo;
    }
}
