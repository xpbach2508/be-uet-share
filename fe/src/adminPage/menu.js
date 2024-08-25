import React from 'react';
import '../css/menu.css';

class Menu extends React.Component {
    
    constructor(props) {
        super(props);
        this.state = {
            color: "#560bad", // Màu mặc định
        };
        this.changeItem = this.changeItem.bind(this);
    }

    changeItem(event) {
        let listButtonOnclick = document.getElementsByClassName('item-menu-onclick');
        listButtonOnclick[0].className = 'item-menu';

        var button = event.target;
        button.className = 'item-menu-onclick';
        // Tách chuỗi bằng ký tự ">"
        // var parts = button.innerHTML.split('>');
        // Lấy phần tử cuối cùng sau khi tách, và loại bỏ các khoảng trắng không cần thiết
        // var lastPart = parts[parts.length - 1].trim();
        this.props.changeItemMenu(button.innerHTML);
    }

    componentDidMount() {
        let home = document.getElementsByClassName('item-menu')[0];
        home.className = 'item-menu-onclick';
    }

    render() {
        return(
            <footer>
                <div id='menu'>
                    <button className='item-menu' onClick={this.changeItem}>Home</button>
                    <button className='item-menu' onClick={this.changeItem}>List taxi</button>
                    <button className='item-menu' onClick={this.changeItem}>Create taxi</button>
                    <button className='item-menu' onClick={this.changeItem}>Create driver</button>
                    <button className='item-menu' onClick={this.changeItem}>Account</button>
                    <button className='item-menu' onClick={this.changeItem}>Log out</button>
                </div>
            </footer>
        )
    }
}

export default Menu