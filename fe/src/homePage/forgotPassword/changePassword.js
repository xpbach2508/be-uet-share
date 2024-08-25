import React from "react";
import { URL } from "../../common";
import { Navigate } from 'react-router-dom';

class ChangePassword extends React.Component {

    constructor(props) {
        super(props);
        /*
            access: xác định đăng nhập thành công hay chưa
        */
        this.state = {
            access: false
        }
        this.enterPassword = this.enterPassword.bind(this);
        this.login = this.login.bind(this);
        this.nextLogin = this.nextLogin.bind(this);
    }

    // Thay đổi kiểu hiển thị sang đăng nhập
    login() {
        this.props.changeInterfaceType('Login');
    }

    // Nhập password mới
    enterPassword(event) {
        // Check password and repassword
        event.preventDefault();
        const password = document.getElementById('password').value;
        const repassword = document.getElementById('repassword').value;
        var error = document.getElementsByClassName('errLogin')[0]; // span hiển thị lỗi
        error.innerHTML = '';
        if (!password || !repassword) {
            error.innerHTML = 'Please fill in complete information';
            return;
        } else if (password !== repassword) {
            error.innerHTML = 'Repassword incorrect';
            return;
        }

        // Send request
        var email = this.props.email;
        const xmlHttp = new XMLHttpRequest();
        var root = this;
        xmlHttp.onreadystatechange = function () {
            if (this.readyState === 4) {
                if (this.status === 200) {
                    root.nextLogin(email, password, root);
                }
            }
        }
        xmlHttp.open('POST', URL + '/auth/change_password', false);
        xmlHttp.setRequestHeader("Content-type", "application/json");
        const requestData = {
            email: email,
            newPassword: password
        }
        xmlHttp.send(JSON.stringify(requestData))
    }

    nextLogin(email, password, root) {
        const xmlHttp = new XMLHttpRequest();
        xmlHttp.onreadystatechange = function() {
            if (this.readyState === 4) {
                const data = JSON.parse(this.responseText);
                if (this.status === 200) {
                    localStorage.setItem ('jwt', data.jwt);
                    root.setState({
                        access: true
                    })
                }
            }
        }
        xmlHttp.open('GET', URL + '/auth/login?email=' + email + '&password=' + password, false);
        xmlHttp.send(null);
    }

    render() {
        // access là true thì user đăng nhập thành công => chuyển user vào UI làm việc
        if (this.state.access) return <Navigate to='/admin'/>;
        
        return (
            <form className="form_main" onSubmit={this.enterPassword}>
                <p className="heading">Forgot Password</p>
                <div className="inputContainer">
                    <svg viewBox="0 0 16 16" fill="#2e2e2e" height="16" width="16" xmlns="http://www.w3.org/2000/svg" className="inputIcon">
                        <path d="M8 1a2 2 0 0 1 2 2v4H6V3a2 2 0 0 1 2-2zm3 6V3a3 3 0 0 0-6 0v4a2 2 0 0 0-2 2v5a2 2 0 0 0 2 2h6a2 2 0 0 0 2-2V9a2 2 0 0 0-2-2z"></path>
                    </svg>
                    <input placeholder="New password" id="password" className="inputField" type="password" />
                </div>
                <div className="inputContainer">
                    <svg viewBox="0 0 16 16" fill="#2e2e2e" height="16" width="16" xmlns="http://www.w3.org/2000/svg" className="inputIcon">
                        <path d="M8 1a2 2 0 0 1 2 2v4H6V3a2 2 0 0 1 2-2zm3 6V3a3 3 0 0 0-6 0v4a2 2 0 0 0-2 2v5a2 2 0 0 0 2 2h6a2 2 0 0 0 2-2V9a2 2 0 0 0-2-2z"></path>
                    </svg>
                    <input placeholder="Repassword" id="repassword" className="inputField" type="password" />
                </div>
                <span className='errLogin'></span>
                <button type='submit' id="button">Change password</button>
                <div className="signupContainer">
                    <p />
                    <p className='forgot' onClick={this.login}>Login</p>
                </div>
            </form>
        );
    }
}

export default ChangePassword