import React, { Component } from 'react';
import EnergySummary from '../chart/EnergySummary.jsx';
import EnergyConsumptionVsProduction from '../chart/EnergyConsumptionVsProduction.jsx';
import Button from 'react-bootstrap/Button';
import Row from 'react-bootstrap/Row';
import Col from 'react-bootstrap/Col';

class InverterHistory extends Component {

   constructor(props) {
      super(props);
      this.handleNavPrev = this.handleNavPrev.bind(this);
      this.handleNavNext = this.handleNavNext.bind(this);
      this.updateWindowDimensions = this.updateWindowDimensions.bind(this);

      this.state = {
         inverter: [],
         evaluation: [],
         dataChartPower: [],
         dataChartInverter: [],
         selectDate: new Date(),
         prevDate: this.selectPrevious(new Date()),
         nextDate: this.selectNext(new Date()),
         width: window.innerWidth,
         height: window.innerHeight,
         widthChartInverter: this.getInverterChartWidth(window.innerWidth)
      };
   }

   componentDidMount() {
      this.onNavigate(this.state.selectDate);
      window.addEventListener('resize', this.updateWindowDimensions);
   }

   componentWillUnmount() {
      window.removeEventListener('resize', this.updateWindowDimensions);
   }

   updateWindowDimensions() {
      this.setState({
         width: window.innerWidth,
         height: window.innerHeight,
         widthChartInverter: this.getInverterChartWidth(window.innerWidth),
      });
   }

   onNavigate(selectDate) {
      fetch('api/meterdatasummary/' + this.getCurrentDate(selectDate))
         .then(response => response.json())
         .then((data) => {
            this.setState({
               inverter: data,
               evaluation: this.evaluate(data),
               dataChartPower: this.getPowerForChart(data),
               dataChartInverter: this.getEnergyForMins(data.meteringDataMinDtos),
               selectDate: selectDate,
               prevDate: this.selectPrevious(selectDate),
               nextDate: this.selectNext(selectDate)
            })
            console.log(this.state.dataChartInverter);
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
         <div className='boxed'>
            <Row>
               <Col>
                  <img src='images/inverter.png' width='80px' />
               </Col>
               <Col>
                  <h3>inverter</h3>
                  <h3>{this.prettyMonth(this.state.selectDate)}</h3>
                  <div>status: {this.state.inverter.status}</div>
               </Col>
               <Col>
                  <h3>economic</h3>
                  <div>autonomy: {this.state.inverter.autonomy} %</div>
                  <div>cost: {this.state.inverter.cost}</div>
                  <div>income: {this.state.inverter.income}</div>
               </Col>
               <Col>
                  <h3>consumption</h3>
                  <div>average: {this.state.evaluation.avConsumed}</div>
                  <div>min: {this.state.evaluation.miConsumed}</div>
                  <div>max: {this.state.evaluation.mxConsumed}</div>
               </Col>
               <Col>
                  <h3>production</h3>
                  <div>average: {this.state.evaluation.avProduced}</div>
                  <div>min: {this.state.evaluation.miProduced}</div>
                  <div>max: {this.state.evaluation.mxProduced}</div>
               </Col>
               <Col>
                  <h3>feedback</h3>
                  <div>average: {this.state.evaluation.avFeedback}</div>
                  <div>min: {this.state.evaluation.miFeedback}</div>
                  <div>max: {this.state.evaluation.mxFeedback}</div>
               </Col>
            </Row>

            <Row>
               <EnergyConsumptionVsProduction data={this.state.dataChartPower} size={[this.state.widthChartInverter, 80]} />
            </Row>

            <Row>
               <EnergySummary data={this.state.dataChartInverter} size={[this.state.widthChartInverter, 300]}
                  maxwh={this.state.evaluation.maxwh} maxd={this.entriesInSelection(this.state.selectDate)} />
            </Row>

            <Row>
               <Col className="align-self-start">
                  <Button variant="outline-secondary" size='sm' key="prev" onClick={this.handleNavPrev}>&lt; previous</Button>
                  {this.prettyDate(this.state.inverter.fromTime)}
               </Col>
               <Col className="align-self-end text-right">
                  {this.prettyDate(this.state.inverter.untilTime)}
                  <Button variant='outline-secondary' size='sm' key="next" onClick={this.handleNavNext}>next &gt;</Button>
               </Col>
            </Row>

         </div >
      );
   }

   getInverterChartWidth(width) {
      if (width === undefined || width < 640) {
         return 400;
      } else if (width > 1200) {
         return 1100;
      }
      return 600;
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

   selectPrevious(selectDate) {
      let date = this.deltaDate(selectDate, -1, 0);
      return date;
   }

   selectNext(selectDate) {
      let date = this.deltaDate(selectDate, 1, 0);
      return date;
   }

   deltaDate(input, months, years) {
       let date = new Date(input);
       date.setDate(1);
       date.setMonth(date.getMonth() + months);
       date.setFullYear(date.getFullYear() + years);
       return date;
   }

   getEnergyForMins(dto) {
      if (dto == undefined) {
         return [];
      }
      return dto.map(item => [
         this.dayOfMonth(item.startTime),
         item.powerConsumed,
         item.powerProduced,
         item.powerFeedback,
         item.powerProduced - item.powerFeedback,
         item.status
      ]);
   }

   evaluate(data) {
      const dto = data.meteringDataMinDtos;
      const entryCount = dto.length;
      let eva = {
         entries: entryCount,
         maxEntries: this.entriesInSelection(this.state.selectDate), // why not executed again?
         maxwh: 0,
         avProduced: (data.powerProduced / entryCount).toFixed(2),
         avFeedback: (data.powerFeedback / entryCount).toFixed(2),
         avConsumed: (data.powerConsumed / entryCount).toFixed(2),
         avFromgrid: (data.powerFromNetwork / entryCount).toFixed(2),
         avFromprod: (data.powerFromProduction / entryCount).toFixed(2),
         mxProduced: 0,
         mxFeedback: 0,
         mxConsumed: 0,
         miProduced: 100000,
         miFeedback: 100000,
         miConsumed: 100000
      };
      for (const el of dto) {
         if (el.powerProduced > eva.mxProduced)  eva.mxProduced = el.powerProduced;
         if (el.powerProduced < eva.miProduced)  eva.miProduced = el.powerProduced;
         if (el.powerFeedback > eva.mxFeedback)  eva.mxFeedback = el.powerFeedback;
         if (el.powerFeedback < eva.miFeedback)  eva.miFeedback = el.powerFeedback;
         if (el.powerConsumed > eva.mxConsumed)  eva.mxConsumed = el.powerConsumed;
         if (el.powerConsumed < eva.miConsumed)  eva.miConsumed = el.powerConsumed;
      }
      eva.maxwh = (eva.mxConsumed > eva.mxProduced) ? eva.mxConsumed : eva.mxProduced;
      //console.log(eva);
      return eva;
   }

   entriesInSelection(jsonDate) {
      let date = new Date();
      date.setTime(Date.parse(jsonDate));
      let entries = new Date(date.getFullYear(), date.getMonth() + 1, 0).getDate();
      //console.log("entries in json {} selection/days: {}, entries: {}", jsonDate, date, entries);
      return entries;
   }

   dayOfMonth(jsonDate) {
      let date = new Date();
      if (jsonDate == undefined) {
         return date.toLocaleString();
      }
      date.setTime(Date.parse(jsonDate));
      return new Intl.DateTimeFormat("en-GB", {
            day: "2-digit"
         }).format(date);
   }

   prettyMonth(jsonDate) {
      let date = new Date();
      if (jsonDate == undefined) {
         return date.toLocaleString();
      }
      date.setTime(Date.parse(jsonDate));
      return new Intl.DateTimeFormat("en-GB", {
            year: "numeric",
            month: "long"
         }).format(date);
   }

   prettyDate(jsonDate) {
      let date = new Date();
      if (jsonDate == undefined) {
         return date.toLocaleString();
      }
      date.setTime(Date.parse(jsonDate));

      let formatted = ' ';
      if (window.innerWidth > 550) {
         formatted = new Intl.DateTimeFormat("en-GB", {
            year: "numeric",
            month: "long",
            day: "numeric",
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
      return (today.getMonth() + 1) + '.' + today.getFullYear();
   }
}


export default InverterHistory;