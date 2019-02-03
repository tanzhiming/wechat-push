/**
 * Created by tanzhiming on 2018/4/19.
 */
$(document).ready(function() {

});

function openPage(title, url) {

    var isExists = $("#tab").tabs("exists", title);
    if (isExists) {
        var tab = $("#tab").tabs("getTab", title);
        $("#tab").tabs('update', {
            tab: tab,
            options: {
                title: title,
                href: url,
                closable: true
            }
        });
        $("#tab").tabs("select", title);
    } else {
        if (title == "编辑任务") {
            if ($("#tab").tabs("exists", "添加任务")) {
                alert('请先关闭“添加任务”Tab选项卡');
                return;
            }

            if ($("#tab").tabs("exists", "发送报警图片")) {
                alert('请先关闭“发送报警图片”Tab选项卡');
                return;
            }

        }
        if (title == "添加任务") {
            if ($("#tab").tabs("exists", "编辑任务")) {
                alert('请先关闭“编辑任务”Tab选项卡');
                return;
            }

            if ($("#tab").tabs("exists", "发送报警图片")) {
                alert('请先关闭“发送报警图片”Tab选项卡');
                return;
            }
        }

        if (title == "发送报警图片") {
            if ($("#tab").tabs("exists", "编辑任务")) {
                alert('请先关闭“编辑任务”Tab选项卡');
                return;
            }

            if ($("#tab").tabs("exists", "添加任务")) {
                alert('请先关闭“添加任务”Tab选项卡');
                return;
            }
        }

        $("#tab").tabs('add', {
            title: title,
            href: url,
            closable: true
        });
    }
}



function convTaskType(value, row, index) {

    if (value == 1) {
        return "手动任务";
    } else if (value == 2) {
        return "自动任务";
    }
    return "";
}

function exitSystem() {

    window.location.href="/logout";
}

