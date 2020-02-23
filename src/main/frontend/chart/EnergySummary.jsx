import React, { Component } from 'react';
import { scaleLinear, scaleBand } from 'd3-scale';
import { select } from 'd3-selection';
import { axisBottom, axisLeft } from 'd3-axis';

class EnergySummary extends Component {

   constructor(props) {
      super(props);
      this.createBarChart = this.createBarChart.bind(this);
   }

   componentDidMount() {
      this.createBarChart();
   }

   componentDidUpdate() {
      this.createBarChart();
   }

   createBarChart() {
      const node = this.node;
      // max Wh produced or used
      const maxY = this.props.maxwh + 200;
      // max entries (f.e. days in month)
      const maxX = this.props.maxd;
      let margin = { top: 10, right: 10, bottom: 30, left: 30 },
         width = this.props.size[0] - margin.left - margin.right,
         height = this.props.size[1] - margin.top - margin.bottom;
      const xScale = scaleLinear()
         .domain([0, maxX])
         .range([margin.left, margin.left + width - 14]);
      const yScale = scaleLinear()
         .domain([0, maxY])
         .range([height, 0]);

      // cleanup elements inside svg
      select(node)
         .selectAll('g')
         .remove();
      select(node)
         .selectAll('rect')
         .remove();
      select(node)
         .selectAll('circle')
         .remove();
      select(node)
         .selectAll('path')
         .remove();

      const consumedData = this.getArrayForConsumedData(this.props.data);
      const producedData = this.getArrayForFeedbackData(this.props.data);

      // show production
      const rectWidth = (this.props.size[0] > 700) ? 10 : 3;
      select(node)
         .selectAll('svg')
         .data(producedData)
         .enter()
         .append('rect')
         .style('fill', '#fe9922')
         .attr('x', d => xScale(d.time) + 2)
         .attr('y', d => yScale(d.produced) + margin.top)
         .attr('height', d => height - yScale(d.produced))
         .attr('width', rectWidth);
      // show feedback overlay
      if (this.props.size[0] > 700) {
         select(node)
            .selectAll('svg')
            .data(producedData)
            .enter()
            .append('rect')
            .style('fill', '#fff')
            .attr('x', d => xScale(d.time) + 4)
            .attr('y', d => yScale(d.produced) + margin.top + 2)
            .attr('height', d => height - yScale(d.feedback) - 2)
            .attr('width', rectWidth - 4);
      } else {
         select(node)
            .selectAll('svg')
            .data(producedData)
            .enter()
            .append('rect')
            .style('fill', '#bbddbb')
            .attr('x', d => xScale(d.time) + 3)
            .attr('y', d => yScale(d.produced) + margin.top + 2)
            .attr('height', d => height - yScale(d.feedback) - 2)
            .attr('width', rectWidth - 2);
      }

      // show consumption
      select(node)
         .selectAll('svg')
         .data(consumedData)
         .enter()
         .append('rect')
         .style('fill', '#bb4444')
         .attr('x', d => xScale(d.time) - rectWidth)
         .attr('y', d => yScale(d.consumed) + margin.top)
         .attr('height', d => height - yScale(d.consumed))
         .attr('width', rectWidth);
      // show from production overlay
      if (this.props.size[0] > 700) {
         select(node)
            .selectAll('svg')
            .data(consumedData)
            .enter()
            .append('rect')
            .style('fill', '#fff')
            .attr('x', d => xScale(d.time) - rectWidth + 2)
            .attr('y', d => yScale(d.consumed) + margin.top + 2)
            .attr('height', d => height - yScale(d.fromProd) - 2)
            .attr('width', rectWidth - 4);
      } else {
         select(node)
            .selectAll('svg')
            .data(consumedData)
            .enter()
            .append('rect')
            .style('fill', '#bbddbb')
            .attr('x', d => xScale(d.time) - rectWidth + 1)
            .attr('y', d => yScale(d.consumed) + margin.top + 2)
            .attr('height', d => height - yScale(d.fromProd) - 2)
            .attr('width', rectWidth - 2);
      }

      // mark status of values (OK/OK_PARTIAL/NOT_ENOUGH_DATA)
      const status = this.getArrayForStatus(this.props.data);
      select(node)
         .selectAll('svg')
         .data(status.filter(ar => ar.status === 'OK' || ar.status === 'OK_PARTIAL'))
         .enter()
         .append('rect')
         .style('fill', '#00ff00')
         .attr('x', d => xScale(d.time) - rectWidth)
         .attr('y', height + margin.top + 2)
         .attr('height', 2)
         .attr('width', rectWidth + rectWidth + 2)
         .style("opacity", .7);
      select(node)
         .selectAll('svg')
         .data(status.filter(ar => ar.status !== 'OK' && ar.status !== 'OK_PARTIAL' && ar.status !== 'NOT_ENOUGH_DATA'))
         .enter()
         .append('circle')
         .style('stroke', '#666666')
         .style('stroke-width', '2')
         .style('fill', 'none')
         .attr('cx', d => xScale(d.time))
         .attr('cy', height + margin.top + 2)
         .attr('r', 3)
         .style("opacity", .7);
      select(node)
         .selectAll('svg')
         .data(status.filter(ar => ar.status === 'NOT_ENOUGH_DATA'))
         .enter()
         .append('circle')
         .style('stroke', '#bb00ff')
         .style('stroke-width', '2')
         .style('fill', 'none')
         .attr('cx', d => xScale(d.time))
         .attr('cy', height + margin.top + 2)
         .attr('r', 3)
         .style("opacity", .7);

      // prepare axis
      let yAxis = axisLeft()
         .scale(yScale)
         .ticks(12, "s")
         .tickSize(-width, 0, 0);

      const scaleTitle = scaleBand()
         .domain(this.getDayArray(this.props.maxd))
         .range([margin.left, margin.left + width - 14])
         .paddingInner(10);
      let xAxis = axisBottom(scaleTitle)
         .tickSizeOuter(0);

      // show axis
      select(node)
         .append('g')
         .attr('transform', `translate(0,${margin.top + height})`)
         .call(xAxis)
         .selectAll("text")
         .attr("transform", "translate(-5,5)rotate(-45)")
         .style("text-anchor", "end");

      select(node)
         .append('g')
         .attr('transform', `translate(${margin.left},${margin.top})`)
         .call(yAxis)
         .attr("class", "grid");

   }

   render() {
      return (
         <svg ref={node => this.node = node} width={this.props.size[0]} height={this.props.size[1]}>
         </svg>
      )
   }

   getArrayForConsumedData(data) {
      let consumedArray = data.map(el => {
         return {
            time: el[0],
            consumed: el[1],
            fromProd: el[4]
         };
      });
      return consumedArray;
   }

   getArrayForFeedbackData(data) {
      let feedbackArray = data.map(el => {
         return {
            time: el[0],
            produced: el[2],
            feedback: el[3]
         };
      });
      let resultArray = feedbackArray.filter(ar => ar.produced > 0);
      return resultArray;
   }

   getArrayForStatus(data) {
      let statusArray = data.map(el => { return { time: el[0], status: el[5] }; });
      return statusArray;
   }

   getDayArray(entries) {
      let times = [];
      for (let i = 1; i <= entries; i++) {
         //if (this.props.size[0] > 400 || i % 2 !== 0) {
         times[i] = i;
         //}
      }
      return times;
   }
}

export default EnergySummary;