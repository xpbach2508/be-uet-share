import React from "react";
import { URL } from "../../common";

class VerifyOTP extends React.Component {

    constructor(props) {
        super(props);
        this.enterOtp = this.enterOtp.bind(this);
        this.login = this.login.bind(this);
    }

    // Thay đổi kiểu hiển thị sang đăng nhập
    login() {
        this.props.changeInterfaceType('Login');
    }

    // Lấy OTP user nhập vào kiểm tra và gửi lên server
    enterOtp(event) {
        // Check OTP
        event.preventDefault();
        let root = this;
        var otp = document.getElementById('otp').value;
        var error = document.getElementsByClassName('errLogin')[0]; // span hiển thị lỗi
        error.innerHTML = '';
        if (!otp) {
            error.innerHTML = 'You have not entered the OTP code';
            return;
        }

        // Send request
        var email = this.props.email;
        const xmlHttp = new XMLHttpRequest();
        xmlHttp.onreadystatechange = function () {
            if (this.readyState === 4) {
                if (this.status === 200) {
                    // Thành công thì hiển thị UI bước tiếp theo
                    root.props.changeInterfaceForgotType("ChangePassword");
                } else {
                    const data = JSON.parse(this.responseText);
                    error.innerHTML = data;
                }
            }
        }
        xmlHttp.open('GET', URL + '/auth/verify_otp?otp=' + otp + '&email=' + email, false);
        xmlHttp.send(null);
    }

    render() {
        return (
            <form className="form_main" onSubmit={this.enterOtp}>
                <p className="heading">Forgot password</p>
                <div className="inputContainer">
                    <svg viewBox="0 0 16 16" fill="#2e2e2e" height="16" width="16" xmlns="http://www.w3.org/2000/svg" className="inputIcon">
                        <path d="M13.106 7.222c0-2.967-2.249-5.032-5.482-5.032-3.35 0-5.646 2.318-5.646 5.702 0 3.493 2.235 5.708 5.762 5.708.862 0 1.689-.123 2.304-.335v-.862c-.43.199-1.354.328-2.29.328-2.926 0-4.813-1.88-4.813-4.798 0-2.844 1.921-4.881 4.594-4.881 2.735 0 4.608 1.688 4.608 4.156 0 1.682-.554 2.769-1.416 2.769-.492 0-.772-.28-.772-.76V5.206H8.923v.834h-.11c-.266-.595-.881-.964-1.6-.964-1.4 0-2.378 1.162-2.378 2.823 0 1.737.957 2.906 2.379 2.906.8 0 1.415-.39 1.709-1.087h.11c.081.67.703 1.148 1.503 1.148 1.572 0 2.57-1.415 2.57-3.643zm-7.177.704c0-1.197.54-1.907 1.456-1.907.93 0 1.524.738 1.524 1.907S8.308 9.84 7.371 9.84c-.895 0-1.442-.725-1.442-1.914z"></path>
                    </svg>
                    <input placeholder="OTP code" id="otp" className="inputField" type="text" />
                </div>
                <span className='errLogin'></span>
                <button type='submit' id="button">Next</button>
                <div className="signupContainer">
                    <p />
                    <p className='forgot' onClick={this.login}>Login</p>
                </div>
            </form>
        );
    }
}

export default VerifyOTP