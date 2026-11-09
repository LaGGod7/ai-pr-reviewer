class LLMUnavailableError(Exception):
    """Raised when the LLM provider fails, times out or rejects the request."""