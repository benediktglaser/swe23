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
    rangeSelector: {
        buttons: [{
            type: 'day',
            count: 1,
            text: '1d'
        }, {
            type: 'week',
            count: 1,
            text: '1w'
        }, {
            type: 'month',
            count: 1,
            text: '1m'
        }, {
            type: 'year',
            count: 1,
            text: '1y'
        }, {
            type: 'all',
            text: 'All'
        }],
        selected: 4 // The index of the button to be selected by default (e.g. 4 selects the "All" button)
    },
    series: [{
        name: 'My Series',
        data: [1, 3, 2, 4, 3, 5, 4, 6, 5, 7, 6, 8]
    }]
});