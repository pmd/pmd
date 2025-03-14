import logging
# CPD-OFF
logger = logging.getLogger('django.request')
class BaseHandler(object):
    def __init__(self):
        self._request_middleware = None
        # CPD-ON
