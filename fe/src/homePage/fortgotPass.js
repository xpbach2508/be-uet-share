import React from "react";
import ChangePassword from "./forgotPassword/changePassword.js";
import VerifyOTP from "./forgotPassword/verifyOTP";
import CreateOTP from "./forgotPassword/createOTP";

class ForgotPass extends React.Component {

    constructor(props) {
        super(props);
        /*
            interfaceForgotType: xác định UI hiển thị
        */
        this.state = {
            email: "",
            interfaceForgotType: "CreateOTP"
        }
        this.changeInterfaceForgotType = this.changeInterfaceForgotType.bind(this);
        this.changeEmail = this.changeEmail.bind(this);
    }

    changeEmail(event) {
        this.setState({
            email: event
        })
    }

    changeInterfaceForgotType(event) {
        this.setState({
            interfaceForgotType: event
        })
    }

    render() {
        // Dựa vào interfaceType để hiện UI phù hợp
        switch(this.state.interfaceForgotType) {
            case "ChangePassword": return <ChangePassword changeInterfaceForgotType={this.changeInterfaceForgotType} 
                changeInterfaceType={this.props.changeInterfaceType} email={this.state.email}/>
            case "VerifyOTP": return <VerifyOTP changeInterfaceForgotType={this.changeInterfaceForgotType}
                changeInterfaceType={this.props.changeInterfaceType} email={this.state.email}/>
            default: return <CreateOTP changeInterfaceForgotType={this.changeInterfaceForgotType}
                changeInterfaceType={this.props.changeInterfaceType} email={this.state.email} changeEmail={this.changeEmail}/>
        }
    }
}

export default ForgotPass