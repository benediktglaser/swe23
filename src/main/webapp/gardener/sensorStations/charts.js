<script src="https://code.highcharts.com/highcharts.js"></script>
Highcharts.chart('chart-containerr', {
    chart: {
        type: 'line'
    },
    title: {
        text: 'My Chart'
    },
    xAxis: {
        categories: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun',
            'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec']
    },
    yAxis: {
        title: {
            text: 'Value'
        }
    },
    series: [{
        name: 'My Series',
        data: [1, 3, 2, 4, 3, 5, 4, 6, 5, 7, 6, 8]
    }]
});