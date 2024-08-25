import React from 'react';
import Login from './login';
import ForgotPass from './fortgotPass';

class Home extends React.Component {

    constructor(props) {
        super(props);
        /*
            interfaceType: xác định UI hiển thị
        */
        this.state = {
            interfaceType: "Login"
        }
        this.changeInterfaceType = this.changeInterfaceType.bind(this);
    }

    changeInterfaceType(event) {
        this.setState({
            interfaceType: event
        })
    }

    render() {
        // Dựa vào interfaceType để hiện UI phù hợp
        switch(this.state.interfaceType) {
            case "ForgotPassword": return <ForgotPass changeInterfaceType={this.changeInterfaceType}/>
            default: return <Login changeInterfaceType={this.changeInterfaceType}/>
        }
    }
}

export default Home