import React from "react";
import { URL } from "../../common";

class CreateOTP extends React.Component {

    constructor(props) {
        super(props);
        this.enterEmail = this.enterEmail.bind(this);
        this.login = this.login.bind(this);
        this.verificationEmail = this.verificationEmail.bind(this);
    }

    // Kiểm tra định dạng email
    verificationEmail(email) {
        var mailformat = /^\w+([.-]?\w+)*@\w+([.-]?\w+)*(\.\w{2,3})+$/;
        if (email.match(mailformat)) return true;
        else return false;
    }

    // Thay đổi kiểu hiển thị sang đăng nhập
    login() {
        this.props.changeInterfaceType('Login');
    }

    // Lấy email user nhập vào kiểm tra email và gửi request lên server
    enterEmail(event) {
        event.preventDefault();
        let root = this;
        // Check email
        var email = document.getElementById('email').value;
        var error = document.getElementsByClassName('errLogin')[0]; // span hiển hị lỗi
        error.innerHTML = '';
        if (!email) {
            error.innerHTML = 'Please enter email';
            return;
        } else {
            if (!this.verificationEmail(email)) {
                error.innerHTML = 'Email invalidate';
                return;
            }
        }
        
        alert('OTP has been sent to your email');
        root.props.changeEmail(email);
        root.props.changeInterfaceForgotType("VerifyOTP");
        // Send request
        const xmlHttp = new XMLHttpRequest();
        xmlHttp.onreadystatechange = function () {
        }
        xmlHttp.open('GET', URL + '/auth/create_otp?email=' + email, false);
        xmlHttp.send(null);
    }

    render() {
        return (
            <form className="form_main" onSubmit={this.enterEmail}>
                <p className="heading">Forgot password</p>
                <div className="inputContainer">
                    <svg viewBox="0 0 16 16" fill="#2e2e2e" height="16" width="16" xmlns="http://www.w3.org/2000/svg" className="inputIcon">
                        <path d="M13.106 7.222c0-2.967-2.249-5.032-5.482-5.032-3.35 0-5.646 2.318-5.646 5.702 0 3.493 2.235 5.708 5.762 5.708.862 0 1.689-.123 2.304-.335v-.862c-.43.199-1.354.328-2.29.328-2.926 0-4.813-1.88-4.813-4.798 0-2.844 1.921-4.881 4.594-4.881 2.735 0 4.608 1.688 4.608 4.156 0 1.682-.554 2.769-1.416 2.769-.492 0-.772-.28-.772-.76V5.206H8.923v.834h-.11c-.266-.595-.881-.964-1.6-.964-1.4 0-2.378 1.162-2.378 2.823 0 1.737.957 2.906 2.379 2.906.8 0 1.415-.39 1.709-1.087h.11c.081.67.703 1.148 1.503 1.148 1.572 0 2.57-1.415 2.57-3.643zm-7.177.704c0-1.197.54-1.907 1.456-1.907.93 0 1.524.738 1.524 1.907S8.308 9.84 7.371 9.84c-.895 0-1.442-.725-1.442-1.914z"></path>
                    </svg>
                    <input placeholder="Email" id="email" className="inputField" type="text" />
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

export default CreateOTP