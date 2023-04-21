import unittest
import requests
import logger
from unittest.mock import patch
from unittest.mock import MagicMock
from requests.auth import HTTPBasicAuth

import sensordata
import restcontroller

class TestController(unittest.TestCase):
    @patch('requests.post')
    def test_post(self, mock_post):
        data = sensordata.SensorData(1, 2, 3, 4, 5, 6, 7)
        mock_post.return_value.status_code = 201
        response = requests.post('http://localhost:8080/api', json=data)
        logger.log_debug(response)
        self.assertEqual(response.status_code, 201)

    @patch('requests.post')
    def test_rest_controller_success(self, mock_post):
        data = [[1, '2023-04-21 18:07:52.123456', 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13]]
        authHeader = HTTPBasicAuth('id', 'password')
        mock_post.return_value.status_code = 201
        response = restcontroller.post_measurement('http://localhost:8080/api)', data, authHeader)
        self.assertEqual(len(response), 6)

    def test_rest_controller_failure(self):
        data = [[1, '2023-04-21 18:07:52.123456', 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13]]
        authHeader = HTTPBasicAuth('id', 'password')
        response = restcontroller.post_measurement('http://localhost:8080', data, authHeader)
        self.assertEqual(len(response), 0)

if __name__ == '__main__':
    unittest.main()