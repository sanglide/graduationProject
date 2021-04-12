let id;

$(document).ready(function () {
    id=(window.location.href.split('?')[1].split('=')[1]);
    console.log("id="+id)
    getPs();
})
function getPs(){
    getRequest(
        '/cartoon/ps/'+id,
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
    let str="<img src='"+url+data+"'>"
    $('#image-container').html(str);
}