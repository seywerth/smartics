import React, { Component } from 'react';
import { scaleLinear, scaleBand } from 'd3-scale';
import { select } from 'd3-selection';
import { axisBottom, axisLeft } from 'd3-axis';
import { timeMinute } from 'd3-time';
import { max } from 'd3-array';

class EnergyChart extends Component {

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
      const maxY = 5500; //max(this.props.data, d => d[2]);
	  // max entries for one day
	  const maxX = 12 * 24; //max(this.props.data, d => d[0]);
	  let margin = {top: 10, right: 10, bottom: 30, left: 30},
      				width = this.props.size[0] - margin.left - margin.right,
      				height = this.props.size[1] - margin.top - margin.bottom;
	  const xScale = scaleLinear()
		 .domain([0, maxX])
		 .range([margin.left, margin.left + width]);
      const yScale = scaleLinear()
         .domain([0, maxY])
         .range([height, 0]);

   	  select(node)
      	.selectAll('rect')
      	.data(this.props.data)
      	.enter()
      	.append('rect');

   	  select(node)
      	.selectAll('rect')
      	.data(this.props.data)
      		.exit()
      	.remove();

	  select(node)
      	.selectAll('rect')
      	.data(this.props.data)
      	.style('fill', '#fe9922')
      	.attr('x', d => xScale(d[0]))
      	.attr('y', d => yScale(d[2]) + margin.top)
      	.attr('height', d => height - yScale(d[2]))
      	.attr('width', 2);

	  let yAxis = axisLeft()
		.scale(yScale)
		.ticks(10, "s")
		.tickSize(-width, 0, 0);

	  const scaleTitle = scaleBand()
		.domain(this.get24hrsArray())
		.range([margin.left, margin.left + width])
		.paddingInner(10);
	  let xAxis = axisBottom(scaleTitle)
		.tickSizeOuter(0);

	  select(node)
		.append('svg')
		.append('g')
		.attr('transform', `translate(0,${margin.top + height})`)
		.call(xAxis)
		.selectAll("text")
    	.attr("transform", "translate(-5,5)rotate(-45)")
    	.style("text-anchor", "end");

	  select(node)
		.append('svg')
		.append('g')
		.attr('transform', `translate(${margin.left},${margin.top})`)
		.call(yAxis)
		.attr("class", "grid");

	  // show current time pointer
	  select(node)
      	.select('rect')
      	.style('fill', '#4444aa')
      	.attr('x', margin.left + this.getCurrentPos(width))
      	.attr('y', margin.top - 5)
      	.attr('height', height + 15)
      	.attr('width', 1);
    }

	render() {
      	return (
			<svg ref={node => this.node = node} width={this.props.size[0]} height={this.props.size[1]}>
      		</svg>
		)
    }

	getCurrentPos(width) {
		if (width == undefined) {
			return 0;
		}
		const oneMin = width / (60 * 24);
		const date = new Date();
		console.log("currentpos: " + (date.getHours() * 60 + date.getMinutes()) * oneMin);
		return (date.getHours() * 60 + date.getMinutes()) * oneMin;
	}

	get24hrsArray() {
		let times = [];
		for (let i = 0; i <= 24; i++) {
			times[i] = i + "h";
		}
		return times;
	}
}

export default EnergyChart