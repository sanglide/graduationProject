let id;

$(document).ready(function () {
    id=(window.location.href.split('?')[1].split('=')[1]);
    console.log("id="+id)
    getPs();
})
function getPs(){
    getRequest(
        '/cartoon/fix/'+id,
        function (res) {
            var data = res.content||[];
            console.log("后端返回的东西在这里："+res)
            console.log("后端返回的数据在这里"+data)
            getImage(data);
        },
        function (error) {
            alert(JSON.stringify(error));
        }
    );
}
function getImage(data){
    console.log("进入插字符串的方法："+data)
    let url="http://qqd3in7iz.hn-bkt.clouddn.com/"
    let str="<div class=\"border\" >" +
        "<div class='frame' >" +
        "<div class='image' style='background-image: url("+url+data+")'></div></div></div>"
    $('#image-container').html(str);
}
function tiaozheng(){
    window.location.replace("/user/startTiaoZheng");
}
function jinru(){
    window.location.replace("/user/startSelect");
}