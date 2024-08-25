import React from 'react';
import '../../css/account.css';
import {URL} from "../../common";

class Account extends React.Component {
    
    componentDidMount() {
        let email = document.getElementById('email');
        const jwtToken = localStorage.getItem('jwt');

        const xmlHttp = new XMLHttpRequest();
        xmlHttp.onreadystatechange = function() {
            if (this.readyState === 4) {
                const data = JSON.parse(this.responseText);
                if (this.status === 200) {
                    email.value = data.email;
                }
            }
        }
        xmlHttp.open('GET', URL + '/auth/info_admin', false);
        xmlHttp.setRequestHeader('Authorization', `Bearer ${jwtToken}`);
        xmlHttp.send(null);
    }

    render() {
        return(
            <div className="main-content">
                <div className="container mt-7">
                {/* <h2 className="mb-5">My Account Card</h2> */}
                    <div className="row">
                        <div className="col-xl-8 m-auto order-xl-1">
                        <div className="card bg-secondary shadow">
                            <div className="card-header bg-white border-0">
                            <div className="row align-items-center">
                                <div className="col-8">
                                <h3 className="mb-0">Account information</h3>
                                </div>
                                <div className="col-4 text-right">
                                <a href="#!" className="btn btn-sm btn-primary"><i className="fas fa-user"></i></a>
                                </div>
                            </div>
                            </div>
                            <div className="card-body">
                            <form>
                                <div className="pl-lg-4">
                                    <div className="row">
                                        <div className="col-md-12">
                                            <div className="form-group focused">
                                                <label className="form-control-label" htmlFor="input-address">Role</label>
                                                <input className="form-control form-control-alternative" readOnly value="Admin" type="text" />
                                            </div>
                                        </div>
                                    </div>
                                    <div className="row">
                                        <div className="col-md-12">
                                            <div className="form-group focused">
                                                <label className="form-control-label" htmlFor="input-address">Email</label>
                                                <input id="email" className="form-control form-control-alternative" readOnly type="text" />
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                <hr className="my-4" />
                            </form>
                            </div>
                        </div>
                        </div>
                    </div>
                </div>
            </div>
        )
    }
}

export default Account