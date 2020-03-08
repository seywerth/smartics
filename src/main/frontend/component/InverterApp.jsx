import React, { Component } from 'react';
import EnergyCurrent from '../chart/EnergyCurrent.jsx';
import EnergyConsumptionVsProduction from '../chart/EnergyConsumptionVsProduction.jsx';
import EnergyDay from '../chart/EnergyDay.jsx';
import Button from 'react-bootstrap/Button';
import Dropdown from 'react-bootstrap/Dropdown';
import DropdownButton from 'react-bootstrap/DropdownButton';
import Row from 'react-bootstrap/Row';
import Col from 'react-bootstrap/Col';

class InverterApp extends Component {

   constructor(props) {
      super(props);
      this.handleNavPrev = this.handleNavPrev.bind(this);
      this.handleNavNext = this.handleNavNext.bind(this);
      this.updateWindowDimensions = this.updateWindowDimensions.bind(this);
      this.handleUpdateChart = this.handleUpdateChart.bind(this);

      this.state = {
         inverter: [],
         dataChartCurrent: [],
         dataChartPower: [],
         dataChartInverter: [],
         updatechart: 'refresh: 10s',
         selectDate: new Date(),
         prevDate: this.selectPreviousDay(new Date()),
         nextDate: this.selectNextDay(new Date()),
         width: window.innerWidth,
         height: window.innerHeight,
         widthChartCurrent: this.getCurrentChartWidth(window.innerWidth),
         widthChartInverter: this.getInverterChartWidth(window.innerWidth),
         heightChartInverter: this.getInverterChartHeight(window.innerWidth)
      };
   }

   componentDidMount() {
      this.onNavigate(this.state.selectDate);
      this.getCurrentMeterData();
      window.addEventListener('resize', this.updateWindowDimensions);
      // update current metering data every 15 seconds
      this.interval = setInterval(() => this.setState({ current: this.getCurrentMeterData() }), 10000);
   }

   componentWillUnmount() {
      window.removeEventListener('resize', this.updateWindowDimensions);
      clearInterval(this.interval);
   }

   updateWindowDimensions() {
      this.setState({
         width: window.innerWidth,
         height: window.innerHeight,
         widthChartInverter: this.getInverterChartWidth(window.innerWidth),
         heightChartInverter: this.getInverterChartHeight(window.innerWidth),
         widthChartCurrent: this.getCurrentChartWidth(window.innerWidth)
      });
   }

   getCurrentMeterData() {
      fetch('api/meterdatacurrent')
         .then(response => response.json())
         .then((data) => {
            this.setState({
               dataChartCurrent: this.getCurrentForChart(data)
            })
         })
         .catch(console.log)
   }

   onNavigate(selectDate) {
      fetch('api/meterdatasummary/' + this.getCurrentDate(selectDate))
         .then(response => response.json())
         .then((data) => {
            this.setState({
               inverter: data,
               dataChartPower: this.getPowerForChart(data),
               dataChartInverter: this.getEnergyForMins(data.meteringDataMinDtos),
               selectDate: selectDate,
               prevDate: this.selectPreviousDay(selectDate),
               nextDate: this.selectNextDay(selectDate)
            })
         })
         .catch(console.log)
   }

   handleNavPrev(e) {
      e.preventDefault();
      console.log("handleNavPrev: " + this.state.prevDate);
      this.onNavigate(this.state.prevDate);
   }

   handleNavNext(e) {
      e.preventDefault();
      console.log("handleNavNext: " + this.state.nextDate);
      this.onNavigate(this.state.nextDate);
   }

   handleUpdateChart(selected) {
      console.log('handleUpdateChart value: ' + selected);
      clearInterval(this.interval);

      let time = 60000;
      if (selected === '5') {
         time = 5000;
         this.setState({ updatechart: 'refresh: 5s' })
      } else if (selected === '10') {
         time = 10000;
         this.setState({ updatechart: 'refresh: 10s' })
      } else if (selected === '15') {
         time = 15000;
         this.setState({ updatechart: 'refresh: 15s' })
      } else {
         this.setState({ updatechart: 'no refresh' });
      }
      
      if (time < 60000) {
         this.interval = setInterval(() => this.setState({ current: this.getCurrentMeterData() }), time);
      }
   }

   render() {
      return (
         <div className='boxed'>
            <Row>
               <Col className="align-self-start">
                  <img src='images/inverter.png' width='100px' />
               </Col>
               <Col>
                  <EnergyCurrent data={this.state.dataChartCurrent} size={[this.state.widthChartCurrent, 150]} maxwh={5500} />
               </Col>
               <Col>
                  <h3>inverter {this.getCurrentDate(this.state.selectDate)}</h3>
                  <div>status: {this.state.inverter.status}</div>

                  <br />
                  <DropdownButton alignRight variant='outline-secondary' size='sm' title={this.state.updatechart} onSelect={this.handleUpdateChart}>
                     <Dropdown.Item eventKey="5">refresh: 5s</Dropdown.Item>
                     <Dropdown.Item eventKey="10">refresh: 10s</Dropdown.Item>
                     <Dropdown.Item eventKey="15">refresh: 15s</Dropdown.Item>
                     <Dropdown.Item eventKey="DEACTIVATED">no refresh</Dropdown.Item>
                  </DropdownButton>
               </Col>
            </Row>

            <Row>
               <EnergyConsumptionVsProduction data={this.state.dataChartPower} size={[this.state.widthChartInverter, 80]} />
            </Row>

            <Row>
               <EnergyDay data={this.state.dataChartInverter} size={[this.state.widthChartInverter, this.state.heightChartInverter]}
                  showline={this.getCurrentDate(new Date()) == this.getCurrentDate(this.state.selectDate)}
                  maxwh={5500} />
            </Row>

            <Row>
               <Col className="align-self-start">
                  <Button variant="outline-secondary" size='sm' key="prev" onClick={this.handleNavPrev}>
                     &lt; previous day</Button>
                  {this.prettyDate(this.state.inverter.fromTime)}
               </Col>

               <Col className="align-self-end text-right">
                  {this.prettyDate(this.state.inverter.untilTime)}
                  <Button variant='outline-secondary' size='sm' key="next" onClick={this.handleNavNext}>
                     next day &gt;</Button>
               </Col>
            </Row>

         </div >
      );
   }

   getCurrentChartWidth(width) {
      if (width === undefined || width < 640) {
         return 90;
      } else if (width > 1200) {
         return 200;
      }
      return 150;
   }

   getInverterChartWidth(width) {
      if (width === undefined || width < 640) {
         return 400;
      } else if (width > 1200) {
         return 800;
      }
      return 600;
   }

   getInverterChartHeight(width) {
      if (width === undefined || width < 640) {
         return 200;
      } else if (width > 1200) {
         return 300;
      }
      return 200;
   }

   getCurrentForChart(data) {
      //console.log(data);
      return {
         creationTime: data.creationTime,
         status: data.status,
         produced: data.powerProduced.toFixed(0),
         feedback: data.powerFeedback.toFixed(0),
         consumed: data.powerConsumed.toFixed(0),
         fromgrid: data.powerFromNetwork.toFixed(0),
         fromprod: data.powerFromProduction.toFixed(0)
      };
   }

   getPowerForChart(data) {
      //console.log(data);
      return {
         produced: data.powerProduced,
         feedback: data.powerFeedback,
         consumed: data.powerConsumed,
         fromgrid: data.powerFromNetwork,
         fromprod: data.powerFromProduction
      };
   }

   selectPreviousDay(selectDate) {
      if (selectDate == undefined) {
         return;
      }
      const day = selectDate.getDate();
      let newDate = new Date(selectDate);
      newDate.setDate(day - 1);
      return newDate;
   }

   selectNextDay(selectDate) {
      if (selectDate == undefined) {
         return;
      }
      const day = selectDate.getDate();
      let newDate = new Date(selectDate);
      newDate.setDate(day + 1);
      return newDate;
   }

   getEnergyForMins(dto) {
      if (dto == undefined) {
         return [];
      }
      // multiply 5min by 12 to get Wh
      return dto.map(item => [this.prettyTime(item.startTime),
      item.powerConsumed * 12,
      item.powerProduced * 12,
      item.status]);
   }

   prettyTime(jsonDate) {
      let date = new Date();
      date.setTime(Date.parse(jsonDate));
      return Math.round(date.getHours() * 12 + date.getMinutes() / 5);
   }

   prettyDate(jsonDate) {
      let date = new Date();
      if (jsonDate == undefined) {
         return date.toLocaleString();
      }
      date.setTime(Date.parse(jsonDate));

      let formatted = ' ';
      if (window.innerWidth > 550) {
         formatted = new Intl.DateTimeFormat("at-DE", {
            year: "numeric",
            month: "numeric",
            day: "2-digit",
            hour: "2-digit",
            minute: "2-digit"
         }).format(date);
      }
      return ' ' + formatted + ' ';
   }

   getCurrentDate(initDate) {
      let today = new Date();
      if (initDate) {
         today.setTime(initDate);
      }
      return today.getDate() + '.' + (today.getMonth() + 1) + '.' + today.getFullYear();
   }
}


export default InverterApp;