#!/usr/bin/env python3

# -*- coding: utf-8 -*-
# @Time : 2020/3/12 16:39
# @Author : SeptemberHX
# @File : model.py
import threading

from numpy import array, hstack, zeros, vstack
import tensorflow as tf
from tensorflow.keras.models import Sequential
from tensorflow.keras.layers import LSTM
from tensorflow.keras.layers import Dense
from tensorflow.python.keras.layers import RepeatVector, TimeDistributed, Dropout

import config

model_dict = {}  # each types of demands has its own model on each node
history_data = None  # each types of demands has its own history data on each node
full_model = None


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


def full_split_sequence(sequences, n_steps_in, n_steps_out):
    X, y = list(), list()
    for i in range(len(sequences)):
        # find the end of this pattern
        end_ix = i + n_steps_in
        out_end_ix = end_ix + n_steps_out
        # check if we are beyond the dataset
        if out_end_ix > len(sequences):
            break
        # gather input and output parts of the pattern
        seq_x, seq_y = sequences[i:end_ix, :], sequences[end_ix:out_end_ix, :]
        X.append(seq_x)
        y.append(seq_y)
    return array(X), array(y)


class FullPredictor:
    def __init__(self):
        self.df_graph = tf.Graph()
        self.config = tf.ConfigProto()
        self.config.gpu_options.allow_growth = True
        self.df_session = tf.Session(graph=self.df_graph, config=self.config)
        self.last_train_set = None
        tf.keras.backend.set_session(self.df_session)
        with self.df_session.as_default():
            with self.df_graph.as_default():
                self.model = Sequential()
                self.model.add(LSTM(256, activation='relu', input_shape=(config.INPUT_WINDOW_SIZE, config.FEATURES)))
                # self.model.add(Dropout(rate=0.1))
                self.model.add(RepeatVector(config.OUTPUT_WINDOW_SIZE))
                # self.model.add(Dropout(rate=0.1))
                self.model.add(LSTM(256, activation='relu', return_sequences=True))
                # # self.model.add(Dropout(rate=0.1))
                # self.model.add(LSTM(64, activation='relu'))
                self.model.add(TimeDistributed(Dense(100, activation='relu')))
                self.model.add(TimeDistributed(Dense(config.FEATURES)))
                # self.model.add(Dense(100, activation='relu'))
                # self.model.add(Dense(config.FEATURES))
                self.model.compile(optimizer='adam', loss='mse')
        self.history_data = None

    def train(self, x, y):
        with self.df_session.as_default():
            with self.df_graph.as_default():
                if self.last_train_set is None:
                    self.last_train_set = (x, y)
                else:
                    self.last_train_set = (
                        vstack((self.last_train_set[0], x)),
                        vstack((self.last_train_set[1], y))
                    )
                if self.last_train_set[0].shape[0] > config.MAX_TRAIN_SET_SIZE:
                    self.last_train_set = (
                        self.last_train_set[0][-config.MAX_TRAIN_SET_SIZE:],
                        self.last_train_set[1][-config.MAX_TRAIN_SET_SIZE:]
                    )
                print(self.last_train_set)
                self.model.fit(
                    self.last_train_set[0], self.last_train_set[1],
                    epochs=config.TRAIN_EPOCHS, verbose=1, use_multiprocessing=True)

    def predict_and_train(self, value_list):
        """
        :param value_list: arrays[config.INPUT_WINDOW_SIZE * config.FEATURES]
        :return:
        """
        with self.df_session.as_default():
            with self.df_graph.as_default():
                if self.history_data is not None:
                    # predict
                    if len(self.history_data) > config.INPUT_WINDOW_SIZE:
                        X = value_list.reshape((1, config.INPUT_WINDOW_SIZE, config.FEATURES))
                        if self.t is not None:
                            self.t.join()
                        result = self.model.predict(X, verbose=0).tolist()
                    else:
                        result = value_list[-config.OUTPUT_WINDOW_SIZE:].tolist()

                    self.history_data = vstack((
                        self.history_data[-(config.INPUT_WINDOW_SIZE + config.OUTPUT_WINDOW_SIZE - 1):, :],
                        value_list[-config.OUTPUT_WINDOW_SIZE:, :]
                    ))

                    # training with the new data
                    # X = vstack((self.history_data, value_list[-config.OUTPUT_WINDOW_SIZE:, :]))
                    X, y = full_split_sequence(self.history_data, config.INPUT_WINDOW_SIZE, config.OUTPUT_WINDOW_SIZE)
                    self.t = threading.Thread(target=self.train, args=(X, y))
                    self.t.start()
                    # self.model.fit(X, y, epochs=config.TRAIN_EPOCHS, verbose=0)
                else:
                    self.history_data = value_list

                    # prepare data by adding 0
                    result = value_list[-config.OUTPUT_WINDOW_SIZE:].tolist()

                print(result)
                return result

    def predict(self, value_list):
        with self.df_session.as_default():
            with self.df_graph.as_default():
                X = value_list.reshape((1, config.INPUT_WINDOW_SIZE, config.FEATURES))
                if self.t is not None:
                    self.t.join()
                result = self.model.predict(X, verbose=0).tolist()
        return result

    def pre_train(self, value_list):
        self.history_data = value_list
        X, y = full_split_sequence(value_list, config.INPUT_WINDOW_SIZE, config.OUTPUT_WINDOW_SIZE)
        self.t = threading.Thread(target=self.train, args=(X, y))
        self.t.start()


class Predictor:
    def __init__(self):
        self.df_graph = tf.Graph()
        self.config = tf.ConfigProto()
        self.config.gpu_options.allow_growth = True
        self.df_session = tf.Session(graph=self.df_graph, config=self.config)
        tf.keras.backend.set_session(self.df_session)
        with self.df_session.as_default():
            with self.df_graph.as_default():
                self.model = create_lstm_model(config.INPUT_WINDOW_SIZE, config.OUTPUT_WINDOW_SIZE, config.FEATURES)
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
                    X = array(value_list).reshape((config.FEATURES, config.INPUT_WINDOW_SIZE, config.FEATURES))
                    result = self.model.predict(X, verbose=0).tolist()

                    # training with the new data
                    X = list(self.history_data)
                    X.extend(value_list)
                    X, y = split_sequence(X, config.INPUT_WINDOW_SIZE, config.OUTPUT_WINDOW_SIZE)
                    X = X.reshape((X.shape[0], X.shape[1], config.FEATURES))
                    threading.Thread(target=self.train, args=(X, y)).start()
                    # self.model.fit(X, y, epochs=config.TRAIN_EPOCHS, verbose=0)
                else:
                    # prepare data by adding 0
                    X = [0] * config.INPUT_WINDOW_SIZE * config.OUTPUT_WINDOW_SIZE
                    print(X)
                    X.extend(value_list)
                    X, y = split_sequence(X, config.INPUT_WINDOW_SIZE, config.OUTPUT_WINDOW_SIZE)
                    X = X.reshape((X.shape[0], X.shape[1], config.FEATURES))
                    self.model.fit(X, y, epochs=config.TRAIN_EPOCHS, verbose=0)
                    result = self.model.predict(
                        array(value_list).reshape(
                            (config.FEATURES, config.INPUT_WINDOW_SIZE, config.FEATURES))).tolist()

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
            result = model_dict[node_id][demand_id].predict_and_train(json_data[node_id][demand_id])
    return result


def full_predict(json_data):
    global full_model
    if full_model is None:
        full_model = FullPredictor()
    return full_model.predict_and_train(array(json_data['data']).transpose())


def pure_predict(json_data):
    global full_model
    if full_model is None:
        full_model = FullPredictor()
    return full_model.predict(array(json_data['data']).transpose())


def pre_train(json_data):
    global full_model
    if full_model is None:
        full_model = FullPredictor()
    return full_model.pre_train(array(json_data['data']).transpose())


if __name__ == '__main__':
    t = [
        [1, 2, 3, 4, 5, 6, 7, 8, 9, 10],
        [10, 20, 30, 40, 50, 60, 70, 80, 90, 100]
    ]
    t = array(t)
    X = [[0] * config.OUTPUT_WINDOW_SIZE] * t.shape[0]
    X = array(X)
    X = hstack((X, t))
    X, y = full_split_sequence(X, config.INPUT_WINDOW_SIZE, config.OUTPUT_WINDOW_SIZE)
    print(X)
