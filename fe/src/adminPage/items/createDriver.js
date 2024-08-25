import React from 'react';
import '../../css/select.css';
import {URL} from "../../common";

class CreateDriver extends React.Component {

    constructor(props) {
        super(props);
        this.createDriver = this.createDriver.bind(this);
    }

    createDriver(event) {
        event.preventDefault();
        const jwtToken = localStorage.getItem('jwt');
        let email = document.getElementById('email').value;
        let fullName = document.getElementById('full-name').value;
        let password = document.getElementById('password').value;
        let repassword = document.getElementById('repassword').value;
        let phone = document.getElementById('phone').value;
        let carId = document.getElementById('car-id').value;
        let error = document.getElementsByClassName('errCreate')[0]; // span hiển thị lỗi
        error.innerHTML = '';
        if (!email || !fullName || !password || !repassword || !phone || !carId) {
            error.innerHTML = 'Please fill in complete information';
            return;
        }
        if (password !== repassword) {
            error.innerHTML = 'Repassword incorrect';
            return;
        }

        const xmlHttp = new XMLHttpRequest();
        xmlHttp.onreadystatechange = function() {
            if (this.readyState === 4) {
                if (this.status === 200) {
                    alert('Success!');
                } else error.innerHTML = this.responseText;
            }
        }
        xmlHttp.open('POST', URL + '/auth/signup_driver', false);
        xmlHttp.setRequestHeader("Content-type", "application/json");
        xmlHttp.setRequestHeader('Authorization', `Bearer ${jwtToken}`);
        const requestData = {
            email: email,
            fullName: fullName,
            password: password,
            phone: phone,
            carId: carId
        }
        xmlHttp.send(JSON.stringify(requestData));
    }

    componentDidMount() {
        const jwtToken = localStorage.getItem('jwt');
        let carId = document.getElementById('car-id');
        let error = document.getElementsByClassName('errCreate')[0]; // span hiển thị lỗi
        error.innerHTML = '';

        const xmlHttp = new XMLHttpRequest();
        xmlHttp.onreadystatechange = function() {
            if (this.readyState === 4) {
                const data = JSON.parse(this.responseText);
                if (this.status === 200) {
                    let len = data.length;
                    for (let i = 0; i < len; i++) {
                        // Create option
                        var option1 = document.createElement('option');
                        option1.value = data[i].id;
                        option1.text = data[i].nameCar + ' (License plate: ' + data[i].licensePlate + ')';
                        
                        // Add option into select
                        carId.appendChild(option1);
                    }
                } else error.innerHTML = 'Error loading list taxi';
            }
        }
        xmlHttp.open('GET', URL + '/taxi/taxi_empty', false);
        xmlHttp.setRequestHeader('Authorization', `Bearer ${jwtToken}`);
        xmlHttp.send(null);
    }

    render() {
        return (
            <div className="main-content">
                <div className="container mt-7">
                    <div className="row">
                        <div className="col-xl-8 m-auto order-xl-1">
                            <div className="card bg-secondary shadow">
                                <div className="card-header bg-white border-0">
                                    <div className="row align-items-center">
                                        <div className="col-8">
                                            <h3 className="mb-0">Create driver</h3>
                                        </div>
                                        <div className="col-4 text-right">
                                            <a href="#!" className="btn btn-sm btn-primary"><i className="fas fa-user-plus"></i></a>
                                        </div>
                                    </div>
                                </div>
                                <div className="card-body">
                                    <form onSubmit={this.createDriver}>
                                        <div className="pl-lg-4">
                                            <div className="row">
                                                <div className="col-lg-6">
                                                    <div className="form-group focused">
                                                        <label className="form-control-label" htmlFor="email">Email</label>
                                                        <input type="text" id="email" className="form-control form-control-alternative" />
                                                    </div>
                                                </div>
                                                <div className="col-lg-6 col-lg-6-child-2">
                                                    <div className="form-group">
                                                        <label className="form-control-label" htmlFor="full-name">Driver name</label>
                                                        <input type="text" id="full-name" className="form-control form-control-alternative" />
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                        <div className="pl-lg-4">
                                            <div className="row">
                                                <div className="col-lg-6">
                                                    <div className="form-group focused">
                                                        <label className="form-control-label" htmlFor="password">Password</label>
                                                        <input type="password" id="password" className="form-control form-control-alternative" />
                                                    </div>
                                                </div>
                                                <div className="col-lg-6 col-lg-6-child-2">
                                                    <div className="form-group">
                                                        <label className="form-control-label" htmlFor="repassword">Repassword</label>
                                                        <input type="password" id="repassword" className="form-control form-control-alternative" />
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                        <div className="pl-lg-4">
                                            <div className="row">
                                                <div className="col-lg-6">
                                                    <div className="form-group focused">
                                                        <label className="form-control-label" htmlFor="phone">Phone</label>
                                                        <input type="text" id="phone" className="form-control form-control-alternative" />
                                                    </div>
                                                </div>
                                                <div className="col-lg-6 col-lg-6-child-2">
                                                    <div className="form-group">
                                                        <label className="form-control-label" htmlFor="car-id">Taxi</label>
                                                        <select name="cars" id="car-id" className="form-control-select form-control-alternative">
                                                        </select>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                        <span className='errCreate'></span>
                                        <button className="create-taxi" type="submit">
                                            <span className="text">Create</span>
                                        </button>
                                    </form>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        );
    }
}

export default CreateDriver