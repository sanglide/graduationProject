$(document).ready(function () {
    getRequest(
        'test/getAllImageName',
        function (res) {
            console.log(res);
            insertImage(res);
        },
        function (error) {
            alert(JSON.stringify(error));
        }
    );
});
function insertImage(data){
    let url="http://qqd3in7iz.hn-bkt.clouddn.com/";
    let str="";
    for(let i=0;i<data.length;i++){
        let temp=data[i].hash_name;
        console.log(temp);
        str=str+'<li>' +
            '     <img src="'+url+temp+'" class="mini" width="120" height="90" />' +
            '<img src="'+url+temp+'" class="pic"  />' +
            '</li>';
    }
    $('#image-container').html(str);
}