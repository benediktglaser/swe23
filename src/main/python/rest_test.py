from unittest.mock import patch
from requests.auth import HTTPBasicAuth

import sensordata
import restcontroller



@patch("requests.post")
def test_rest_controller_success(mock_post):
    data = [
        [1, "2023-04-21 18:07:52.123456", 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13]
    ]
    auth_header = HTTPBasicAuth("id", "password")
    mock_post.return_value.status_code = 400
    response = restcontroller.post_measurement("http://localhost:8080/api", data, auth_header)
    assert len(response) == 6
