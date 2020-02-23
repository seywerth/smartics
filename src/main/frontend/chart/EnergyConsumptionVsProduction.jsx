import React, { Component } from 'react';
import { scaleLinear } from 'd3-scale';
import { select } from 'd3-selection';
import { axisBottom } from 'd3-axis';

class EnergyConsumptionVsProduction extends Component {

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
      // fixed height
      const maxY = this.props.size[1];
      // total Wh: fromgrid + fromprod + feedback
      const maxX = this.props.data.fromgrid + this.props.data.fromprod + this.props.data.feedback;
      let margin = { top: 16, right: 10, bottom: 30, left: 30 },
         width = this.props.size[0] - margin.left - margin.right,
         height = this.props.size[1] - margin.top - margin.bottom;
      const xScale = scaleLinear()
         .domain([0, maxX])
         .range([margin.left, margin.left + width]);
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
         .selectAll('text')
         .remove();

      // might be initialized at this.props.data = [], producing an error at width
      if (this.props.data.consumed !== undefined) {
         // show consumption
         select(node)
            .selectAll('svg')
            .data([this.props.data.consumed])
            .enter()
            .append('rect')
            .style('stroke', '#bb4444')
            .style('stroke-width', '2')
            .style('fill', 'none')
            .attr('x', margin.left + 1)
            .attr('y', margin.top)
            .attr('height', yScale(20))
            .attr('width', xScale(this.props.data.consumed) - margin.left);

         // show production
         const prodWidth = xScale(this.props.data.produced) - margin.left;
         if (this.props.data.produced > 0) {
            select(node)
               .selectAll('svg')
               .data([this.props.data.produced])
               .enter()
               .append('rect')
               .style('stroke', '#fe9922')
               .style('stroke-width', '2')
               .style('fill', 'none')
               .attr('x', xScale(this.props.data.fromgrid))
               .attr('y', yScale(70) + margin.top)
               .attr('height', yScale(20))
               .attr('width', prodWidth);
         }
      }

      // show text
      const consumedText = (this.isLarge()) ? 'consumed.. ' + this.formatToKWh(this.props.data.consumed) : '- ' + this.formatToKWh(this.props.data.consumed);
      const consStartX = margin.left + 3;
      select(node)
         .selectAll('svg')
         .data([this.props.data.consumed])
         .enter()
         .append('text')
         .text(consumedText)
         .style('fill', '#bb4444')
         .style('font-size', '1em')
         .style('font-weight', 'bold')
         .attr('x', consStartX)
         .attr('y', margin.top - 4);

      const fromGridText = (this.isLarge()) ? 'from grid...... ' + this.formatToKWh(this.props.data.fromgrid) : '- ' + this.formatToKWh(this.props.data.fromgrid);
      select(node)
         .selectAll('svg')
         .data([this.props.data.fromgrid])
         .enter()
         .append('text')
         .text(fromGridText)
         .style('fill', '#bb4444')
         .style('font-size', '1em')
         .attr('x', consStartX + 2)
         .attr('y', margin.top + 15);

      if (this.props.data.produced > 0) {
         const producedText = (this.isLarge()) ? 'produced.. ' + this.formatToKWh(this.props.data.produced) : '+ ' + this.formatToKWh(this.props.data.produced);
         const prodEndX = (this.isLarge()) ? xScale(maxX) - margin.left - 134 : xScale(maxX) - margin.left - 66;
         select(node)
            .selectAll('svg')
            .data([this.props.data.produced])
            .enter()
            .append('text')
            .text(producedText)
            .style('fill', '#fe9922')
            .style('font-size', '1em')
            .style('font-weight', 'bold')
            .attr('x', prodEndX)
            .attr('y', margin.top);
   
         const feedbackText =  (this.isLarge()) ? 'feedback..... ' + this.formatToKWh(this.props.data.feedback) : '+ ' + this.formatToKWh(this.props.data.feedback);
         select(node)
            .selectAll('svg')
            .data([this.props.data.feedback])
            .enter()
            .append('text')
            .text(feedbackText)
            .style('fill', '#fe9922')
            .style('font-size', '1em')
            .attr('x', prodEndX)
            .attr('y', margin.top + 20);
      }

      // show axis
      let xAxis = axisBottom(xScale)
         .ticks(12, "s")
         .tickSizeOuter(0);

      select(node)
         .append('g')
         .attr('transform', `translate(0,${margin.top + height})`)
         .call(xAxis)
         .selectAll("text");

   }

   render() {
      return (
         <svg ref={node => this.node = node} width={this.props.size[0]} height={this.props.size[1]}>
         </svg>
      )
   }

   isLarge() {
      return this.props.size[0] > 550;      
   }

   formatToKWh(number) {
      let reformat = 0;
      if (number !== undefined) {
         reformat = number / 1000;
      }
      return reformat.toFixed(2) + " kWh";
   }

}

export default EnergyConsumptionVsProduction;