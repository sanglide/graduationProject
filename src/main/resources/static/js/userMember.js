$(document).ready(function () {
    // alertWin("test test test");
    getVIP();
    getCoupon();
});
function alertWin(message) {
    console.log(message);
    var meStr="<h3><span class='label label-danger' style='margin-left:600px;color:white;position:absolute;z-index:99999; top: 80px;'" +
        ">"+message+"</span></h3>"
    $('#alertWindow').html(meStr);
    $("#alertWindow").show().delay(1500).hide(50);
    // $("#alertWindow").show();
}
var isBuyState = true;
var vipCardId;

function getVIP() {
    getRequest(
        '/vip/' + sessionStorage.getItem('id') + '/get',
        function (res) {
            if (res.success) {
                // 是会员
                $("#member-card").css("visibility", "visible");
                $("#member-card").css("display", "");
                $("#nonmember-card").css("display", "none");

                vipCardId = res.content.id;
                $("#member-id").text(res.content.id);
                $("#member-balance").text("¥" + res.content.balance.toFixed(2));
                $("#member-joinDate").text(res.content.joinDate.substring(0, 10));
            } else {
                // 非会员
                $("#member-card").css("display", "none");
                $("#nonmember-card").css("display", "");
            }
        },
        function (error) {
            alertWin(error);
        });

    getRequest(
        '/vip/getVIPInfo',
        function (res) {
            if (res.success) {
                $("#member-buy-price").text(res.content.price);
                $("#member-buy-description").text("充值优惠：每满" + res.content.charge+"送"+res.content.bonus + "。永久有效");
                $("#member-description").text("每满" + res.content.charge+"送"+res.content.bonus);
            } else {
                alertWin(res.content);
            }

        },
        function (error) {
            alertWin(error);
        });
}

function buyClick() {
    clearForm();
    $('#buyModal').modal('show')
    $("#userMember-amount-group").css("display", "none");
    isBuyState = true;
}

function chargeClick() {
    clearForm();
    $('#buyModal').modal('show')
    $("#userMember-amount-group").css("display", "");
    isBuyState = false;
}

function clearForm() {
    $('#userMember-form input').val("");
    $('#userMember-form .form-group').removeClass("has-error");
    $('#userMember-form p').css("visibility", "hidden");
}

function confirmCommit() {
    if (validateForm()) {
        if ($('#userMember-cardNum').val() === "123123123" && $('#userMember-cardPwd').val() === "123123") {
            if (isBuyState) {
                postRequest(
                    '/vip/add?userId=' + sessionStorage.getItem('id'),
                    null,
                    function (res) {
                        $('#buyModal').modal('hide');
                        alertWin("购买会员卡成功");
                        getVIP();
                    },
                    function (error) {
                        alertWin(error);
                    });
            } else {
                postRequest(
                    '/vip/charge',
                    {vipId: vipCardId, amount: parseInt($('#userMember-amount').val())},
                    function (res) {
                        $('#buyModal').modal('hide');
                        alertWin("充值成功");
                        getVIP();
                    },
                    function (error) {
                        alertWin(error);
                    });
            }
        } else {
            alertWin("银行卡号或密码错误");
        }
    }
}

function validateForm() {
    var isValidate = true;
    if (!$('#userMember-cardNum').val()) {
        isValidate = false;
        $('#userMember-cardNum').parent('.form-group').addClass('has-error');
        $('#userMember-cardNum-error').css("visibility", "visible");
    }
    if (!$('#userMember-cardPwd').val()) {
        isValidate = false;
        $('#userMember-cardPwd').parent('.form-group').addClass('has-error');
        $('#userMember-cardPwd-error').css("visibility", "visible");
    }
    if (!isBuyState && (!$('#userMember-amount').val() || parseInt($('#userMember-amount').val()) <= 0)) {
        isValidate = false;
        $('#userMember-amount').parent('.form-group').addClass('has-error');
        $('#userMember-amount-error').css("visibility", "visible");
    }
    return isValidate;
}

function getCoupon() {
    getRequest(
        '/coupon/' + sessionStorage.getItem('id') + '/get',
        function (res) {
            if (res.success) {
                console.log(res);
                var couponList = res.content;
                var couponListContent = '';
                for (var coupon in couponList) {
                    couponListContent += '<div class="col-md-6 coupon-wrapper"><div class="coupon"><div class="content">' +
                        '<div class="col-md-8 left">' +
                        '<div class="name">' +
                        coupon.name +
                        '</div>' +
                        '<div class="description">' +
                        coupon.description +
                        '</div>' +
                        '<div class="price">' +
                        '满' + coupon.targetAmount + '减' + coupon.discountAmount +
                        '</div>' +
                        '</div>' +
                        '<div class="col-md-4 right">' +
                        '<div>有效日期：</div>' +
                        '<div>' + formatDate(coupon.startTime) + ' ~ ' + formatDate(coupon.endTime) + '</div>' +
                        '</div></div></div></div>'
                }
                $('#coupon-list').html(couponListContent);

            }
        },
        function (error) {
            alertWin(error);
        });
}

function formatDate(date) {
    return date.substring(5, 10).replace("-", ".");
}