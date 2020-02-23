import React, { Component } from 'react';
import ReactDOM from 'react-dom';
import ChargerApp from './component/ChargerApp.jsx';
import InverterApp from './component/InverterApp.jsx';
import InverterHistory from './component/InverterHistory.jsx';
import Container from 'react-bootstrap/Container';
import Row from 'react-bootstrap/Row';
import Col from 'react-bootstrap/Col';
import Nav from 'react-bootstrap/Nav';
import Navbar from 'react-bootstrap/Navbar';
import { IndexLinkContainer, LinkContainer } from 'react-router-bootstrap';
import { BrowserRouter as Router, Switch, Route } from "react-router-dom";

import './scss/custom.scss';

class Index extends Component {
   render() {
      return (
         <Router>
         <Container fluid>
            <Row>
               <Col>
                  <Navbar bg="light" expand="md" fixed="top" collapseOnSelect>
                     <IndexLinkContainer to="/">
                        <Navbar.Brand>
                           <img src="images/logo.png" width="26" height="30" className="d-inline-block align-top" alt="logo" />
                           {' '}smartics
                        </Navbar.Brand>
                     </IndexLinkContainer>
                     <Navbar.Toggle aria-controls="basic-navbar-nav" />
                     <Navbar.Collapse id="basic-navbar-nav">
                        <Nav className="mr-auto">
                           <LinkContainer to="/history.jsx">
                              <Nav.Link>history</Nav.Link>
                           </LinkContainer>
                           <LinkContainer to="/settings.jsx">
                              <Nav.Link disabled>settings</Nav.Link>
                           </LinkContainer>
                           <LinkContainer to="/about.jsx">
                              <Nav.Link>about</Nav.Link>
                           </LinkContainer>
                        </Nav>
                     </Navbar.Collapse>
                  </Navbar>
               </Col>
            </Row>
            <Row>
               <Switch>
                  <Route exact path="/">
                     <Col lg={4}>
                        <ChargerApp />
                     </Col>
                     <Col lg={8}>
                        <InverterApp />
                     </Col>
                  </Route>
                  <Route exact path="/history.jsx">
                     <Col lg={12}>
                        <InverterHistory />
                     </Col>
                  </Route>
                  <Route exact path="/about.jsx">
                     <div className='boxed'>
                        Application developed Dec. 2019 until Mar. 2020.<br />Current version: 0.1.5
                     </div>
                  </Route>
               </Switch>
            </Row>
         </Container>
         </Router>
      );
   }
}


export default Index;

const rootElement = document.getElementById('root');
ReactDOM.render(<Index />, rootElement);