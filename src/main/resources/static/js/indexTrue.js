function startCamera(){
    console.log("调用人脸拟合的方法")
    getRequest(
        '/photo',
        function (res) {
            var data = res.content||[];
            console.log("后端返回的东西在这里："+res)
            console.log("后端返回的数据在这里"+data)
        },
        function (error) {
            alert(JSON.stringify(error));
        }
    );
}