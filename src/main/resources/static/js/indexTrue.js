function startCamera(){
    console.log("调用人脸拟合的方法")
    getRequest(
        '/photo',
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
    document.getElementById('camera-button1').style.display = "none";
    // document.getElementById('camera-button2').style.display = "none";
    document.getElementById('camera-button3').style.display = "block";
    document.getElementById('camera-button4').style.display = "block";
}
function picturePs(){
    window.location.replace("/user/selectWhichPs");
}
function pictureFix(){
    window.location.replace("/user/selectWhichFix");
}