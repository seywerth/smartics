import React, { Component } from 'react';
import EnergyChart from './EnergyChart.jsx';
import PowerChart from './PowerChart.jsx';
import Button from 'react-bootstrap/Button';

class InverterApp extends Component {

   constructor(props) {
      super(props);
      this.handleNavPrev = this.handleNavPrev.bind(this);
      this.handleNavNext = this.handleNavNext.bind(this);
      this.updateWindowDimensions = this.updateWindowDimensions.bind(this);
      this.state = {
         inverter: [],
         selectDate: new Date(),
         prevDate: this.selectPreviousDay(new Date()),
         nextDate: this.selectNextDay(new Date()),
         width: window.innerWidth,
         height: window.innerHeight
      };
   }

   componentDidMount() {
      this.onNavigate(this.state.selectDate);
      window.addEventListener('resize', this.updateWindowDimensions);
   }

   updateWindowDimensions() {
      this.setState({
         width: window.innerWidth,
         height: window.innerHeight
      });
   }

   onNavigate(selectDate) {
      fetch('api/meterdatasummary/' + this.getCurrentDate(selectDate))
         .then(response => response.json())
         .then((data) => {
            this.setState({
               inverter: data,
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

   render() {
      return (
         <div>
            <table width='100%'>
               <thead></thead>
               <tbody>
                  <tr>
                     <td><img src='images/inverter.png' width='100px' /></td>
                     <td>
                        <h3>inverter {this.getCurrentDate(this.state.selectDate)}</h3>
                        <div>status: {this.state.inverter.status}</div>
                        <br />
                        <div>produced: {this.state.inverter.powerProduced}</div>
                        <div>consumed: {this.state.inverter.powerConsumed}</div>
                        <div>autonomy: {this.state.inverter.autonomy}</div>
                     </td>
                  </tr>
                  <tr>
                     <td colSpan='2'>
                        <div>
                           <PowerChart data={this.getPowerForChart(this.state.inverter)} size={[this.getComponentWidth(), 80]} />
                        </div>

                        <div>
                           <EnergyChart data={this.getEnergyForMins(this.state.inverter.meteringDataMinDtos)} size={[this.getComponentWidth(), 300]}
                              showline={this.getCurrentDate(new Date()) == this.getCurrentDate(this.state.selectDate)}
                              maxwh={5500} />
                        </div>
                     </td>
                  </tr>
                  <tr>
                     <td>
                        <Button variant="outline-secondary" key="prev" onClick={this.handleNavPrev}>
                           &lt; previous day</Button>
                        {this.prettyDate(this.state.inverter.fromTime)}
                     </td>
                     <td>
                        {this.prettyDate(this.state.inverter.untilTime)}
                        <Button variant="outline-secondary" key="next" onClick={this.handleNavNext}>
                           next day &gt;</Button>
                     </td>
                  </tr>
               </tbody>
            </table>
         </div >
      );
   }

   getComponentWidth() {
      if (this.state.width !== undefined && this.state.width > 1200) {
         return 800;
      }
      return 400;
   }

   getPowerForChart(data) {
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
      if (this.getComponentWidth() > 550) {
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


export default InverterApp