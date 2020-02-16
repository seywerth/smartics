import React, { Component } from 'react';
import ReactDOM from 'react-dom';
import ChargerApp from './component/ChargerApp.jsx';
import InverterApp from './component/InverterApp.jsx';
import Container from 'react-bootstrap/Container';
import Row from 'react-bootstrap/Row';
import Col from 'react-bootstrap/Col';
import Nav from 'react-bootstrap/Nav';
import Navbar from 'react-bootstrap/Navbar';

import './scss/custom.scss';

class Index extends Component {
   render() {
      return (
         <Container fluid>
            <Row>
               <Col>
                  <Navbar bg="light" expand="md" fixed="top" collapseOnSelect>
                     <Navbar.Brand href="#home">
                        <img src="images/logo.png" width="26" height="30" className="d-inline-block align-top" alt="logo" />
                        {' '}smartics
                     </Navbar.Brand>
                     <Navbar.Toggle aria-controls="basic-navbar-nav" />
                     <Navbar.Collapse id="basic-navbar-nav">
                        <Nav className="mr-auto">
                           <Nav.Link href="#history">history</Nav.Link>
                           <Nav.Link href="#settings">settings</Nav.Link>
                           <Nav.Link href="#about">about</Nav.Link>
                        </Nav>
                     </Navbar.Collapse>
                  </Navbar>
               </Col>
            </Row>
            <Row>
               <Col lg={4}>
                  <ChargerApp />
               </Col>
               <Col lg={8}>
                  <InverterApp />
               </Col>
            </Row>
         </Container>
      );
   }
}


export default Index;

const rootElement = document.getElementById('root');
ReactDOM.render(<Index />, rootElement);