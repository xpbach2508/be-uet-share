import React from 'react';
import '../../css/account.css';
import {URL} from "../../common";

class TaxiDetail extends React.Component {

    constructor(props) {
        super(props);
        this.back = this.back.bind(this);
    }
    
    back() {
        this.props.changeItemMenu('List taxi');
    }

    componentDidMount() {
        const jwtToken = localStorage.getItem('jwt');
        const xmlHttp = new XMLHttpRequest();
        xmlHttp.onreadystatechange = function() {
            if (this.readyState === 4) {
                const data = JSON.parse(this.responseText);
                if (this.status === 200) {
                    let carName = document.getElementById('car-name');
                    let seat = document.getElementById('seat');
                    let licensePlate = document.getElementById('license-plate');
                    let email = document.getElementById('email');
                    let driverName = document.getElementById('driver-name');
                    let phone = document.getElementById('phone');
                    let carId = document.getElementById('car-id');

                    carName.value = data.nameCar;
                    seat.value = data.seat + ' seat';
                    licensePlate.value = data.licensePlate;
                    email.value = data.email;
                    driverName.value = data.fullName;
                    phone.value = data.phone;
                    carId.value = data.carId;
                }
            }
        }
        xmlHttp.open('GET', URL + '/driver/find_by_id?driverId=' + this.props.driverId, false);
        xmlHttp.setRequestHeader('Authorization', `Bearer ${jwtToken}`);
        xmlHttp.send(null);
    }
    
    render() {
        return(
            <div className="main-content">
                <div className="container mt-7">
                <div className="row">
                    <div className="col-xl-8 m-auto order-xl-1">
                    <div className="card bg-secondary shadow">
                        <div className="card-header bg-white border-0">
                            <div className="row align-items-center">
                                <div className="col-8">
                                    <h3 className="mb-0">Taxi detail</h3>
                                </div>
                                <div className="col-4 text-right">
                                    <a href="#!" className="btn btn-sm btn-primary" onClick={this.back}><i className="fas fa-arrow-alt-circle-left"></i></a>
                                </div>
                            </div>
                        </div>
                        <div className="card-body">
                            <form>
                                <h6 className="heading-small text-muted mb-4">Taxi</h6>
                                <div className="pl-lg-4">
                                    <div className="row">
                                        <div className="col-lg-6">
                                            <div className="form-group focused">
                                                <label className="form-control-label" htmlFor="car-id">ID</label>
                                                <input type="text" id="car-id" className="form-control form-control-alternative" readOnly />
                                            </div>
                                        </div>
                                        <div className="col-lg-6 col-lg-6-child-2">
                                            <div className="form-group">
                                                <label className="form-control-label" htmlFor="car-name">Taxi name</label>
                                                <input type="text" id="car-name" className="form-control form-control-alternative" readOnly />
                                            </div>
                                        </div>
                                    </div>
                                    <div className="row">
                                        <div className="col-lg-6">
                                            <div className="form-group focused">
                                                <label className="form-control-label" htmlFor="seat">Capacity</label>
                                                <input type="text" id="seat" className="form-control form-control-alternative" readOnly />
                                            </div>
                                        </div>
                                        <div className="col-lg-6 col-lg-6-child-2">
                                            <div className="form-group focused">
                                                <label className="form-control-label" htmlFor="license-plate">License plate</label>
                                                <input type="text" id="license-plate" className="form-control form-control-alternative" readOnly />
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                <hr className="my-4" />
                                <h6 className="heading-small text-muted mb-4">Taxi driver</h6>
                                <div className="pl-lg-4">
                                    <div className="row">
                                        <div className="col-lg-6">
                                            <div className="form-group focused">
                                                <label className="form-control-label" htmlFor="driver-id">ID</label>
                                                <input type="text" id="driver-id" className="form-control form-control-alternative" readOnly value={this.props.driverId} />
                                            </div>
                                        </div>
                                        <div className="col-lg-6 col-lg-6-child-2">
                                            <div className="form-group">
                                                <label className="form-control-label" htmlFor="email">Email</label>
                                                <input type="text" id="email" className="form-control form-control-alternative" readOnly />
                                            </div>
                                        </div>
                                    </div>
                                    <div className="row">
                                        <div className="col-lg-6">
                                            <div className="form-group focused">
                                                <label className="form-control-label" htmlFor="driver-name">Full name</label>
                                                <input type="text" id="driver-name" className="form-control form-control-alternative" readOnly />
                                            </div>
                                        </div>
                                        <div className="col-lg-6 col-lg-6-child-2">
                                            <div className="form-group focused">
                                                <label className="form-control-label" htmlFor="phone">Phone</label>
                                                <input type="text" id="phone" className="form-control form-control-alternative" readOnly />
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

export default TaxiDetail