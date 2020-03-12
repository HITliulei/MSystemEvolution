#!/usr/bin/env python3

# -*- coding: utf-8 -*-
# @Time : 2020/3/12 16:39
# @Author : SeptemberHX
# @File : model.py
import threading

from numpy import array
import tensorflow as tf
from tensorflow.keras.models import Sequential
from tensorflow.keras.layers import LSTM
from tensorflow.keras.layers import Dense

import config

model_dict = {}  # each types of demands has its own model on each node
history_data = {}  # each types of demands has its own history data on each node


def create_lstm_model(n_steps_in, n_steps_out, n_features):
    # define model
    model = Sequential()
    model.add(LSTM(100, activation='relu', return_sequences=True, input_shape=(n_steps_in, n_features)))
    model.add(LSTM(100, activation='relu'))
    model.add(Dense(n_steps_out))
    model.compile(optimizer='adam', loss='mse')
    return model


def split_sequence(sequence, n_steps_in, n_steps_out):
    """
    split a univariate sequence into samples
                                            [10 20 30] [40 50]
                                            [20 30 40] [50 60]
    [10 20 30 40 50 60 70 80 90]   -->      [30 40 50] [60 70]
                                            [40 50 60] [70 80]
                                            [50 60 70] [80 90]
    :param sequence:
    :param n_steps_in:
    :param n_steps_out:
    :return:
    """
    X, y = list(), list()
    for i in range(len(sequence)):
        # find the end of this pattern
        end_ix = i + n_steps_in
        out_end_ix = end_ix + n_steps_out
        # check if we are beyond the sequence
        if out_end_ix > len(sequence):
            break
        # gather input and output parts of the pattern
        seq_x, seq_y = sequence[i:end_ix], sequence[end_ix:out_end_ix]
        X.append(seq_x)
        y.append(seq_y)
    return array(X), array(y)


class Predictor:
    def __init__(self):
        self.df_graph = tf.Graph()
        self.config = tf.ConfigProto()
        self.config.gpu_options.allow_growth = True
        self.df_session = tf.Session(graph=self.df_graph, config=self.config)
        tf.keras.backend.set_session(self.df_session)
        with self.df_session.as_default():
            with self.df_graph.as_default():
                self.model = create_lstm_model(config.INPUT_SIZE, config.OUTPUT_SIZE, config.FEATURES)
        self.history_data = []

    def train(self, x, y):
        with self.df_session.as_default():
            with self.df_graph.as_default():
                self.model.fit(x, y, epochs=config.TRAIN_EPOCHS, verbose=0)

    def predict(self, value_list):
        with self.df_session.as_default():
            with self.df_graph.as_default():
                if len(self.history_data) != 0:
                    # predict
                    X = array(value_list).reshape((1, config.INPUT_SIZE, config.FEATURES))
                    result = self.model.predict(X, verbose=0).tolist()

                    # training with the new data
                    X = list(self.history_data)
                    X.extend(value_list)
                    X, y = split_sequence(X, config.INPUT_SIZE, config.OUTPUT_SIZE)
                    X = X.reshape((X.shape[0], X.shape[1], config.FEATURES))
                    threading.Thread(target=self.train, args=(X, y)).start()
                    # self.model.fit(X, y, epochs=config.TRAIN_EPOCHS, verbose=0)
                else:
                    # prepare data by adding 0
                    X = [0] * config.OUTPUT_SIZE * config.TRAIN_EPOCHS
                    X.extend(value_list)
                    X, y = split_sequence(X, config.INPUT_SIZE, config.OUTPUT_SIZE)
                    X = X.reshape((X.shape[0], X.shape[1], config.FEATURES))
                    self.model.fit(X, y, epochs=config.TRAIN_EPOCHS, verbose=0)
                    result = self.model.predict(array(value_list).reshape((1, config.INPUT_SIZE, config.FEATURES))).tolist()

                # update the history data
                self.history_data = value_list
                return result


def predict(json_data):
    result = {}
    for node_id in json_data:
        result[node_id] = {}
        for demand_id in json_data[node_id]:
            if node_id not in model_dict:
                model_dict[node_id] = {}
            if demand_id not in model_dict[node_id]:
                model_dict[node_id][demand_id] = Predictor()
            result = model_dict[node_id][demand_id].predict(json_data[node_id][demand_id])
    return result
