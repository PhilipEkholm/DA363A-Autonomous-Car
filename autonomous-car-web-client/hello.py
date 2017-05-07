"""
    jQuery Example
    ~~~~~~~~~~~~~~
    A simple application that shows how Flask and jQuery get along.
    :copyright: (c) 2015 by Armin Ronacher.
    :license: BSD, see LICENSE for more details.
"""
from flask import Flask, jsonify, render_template, request
app = Flask(__name__)


@app.route('/_add_numbers')
def add_numbers():
    """Add two numbers server side, ridiculous but well..."""
    key = request.args.get('key', 0)
    return jsonify(result = key)


@app.route('/')
def index():
    return render_template('index.html')

if __name__ == '__main__':
    app.run()