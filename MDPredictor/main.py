#!/usr/bin/env python3

# -*- coding: utf-8 -*-
# @Time : 2020/3/12 15:33
# @Author : SeptemberHX
# @File : main.py

import json
from flask import Flask
from flask import request

import model

app = Flask(__name__)


@app.route('/predict', methods=['POST'])
def predict():
    """
    json_data: {
      "demandId1": [n1, n2, n3, ..., nn],
      "demandId2": [.., .., .., ..., ..],
      ......
    }

    :return: Map[int, List[int]]
    """
    json_data = json.loads(request.get_data().decode('utf-8'))
    return json.dumps(model.predict(json_data))


@app.route('/full_predict', methods=['POST'])
def full_predict():
    """
    json_data: {
      "demandId1": [n1, n2, n3, ..., nn],
      "demandId2": [.., .., .., ..., ..],
      ......
    }

    :return: Map[int, List[int]]
    """
    json_data = json.loads(request.get_data().decode('utf-8'))
    return json.dumps(model.full_predict(json_data))


@app.route('/pre_train', methods=['POST'])
def pre_train():
    """
    json_data: {
      "demandId1": [n1, n2, n3, ..., nn],
      "demandId2": [.., .., .., ..., ..],
      ......
    }

    :return: Map[int, List[int]]
    """
    json_data = json.loads(request.get_data().decode('utf-8'))
    return json.dumps(model.pre_train(json_data))


if __name__ == '__main__':
    app.run(host='0.0.0.0', port=22222, debug=True)
