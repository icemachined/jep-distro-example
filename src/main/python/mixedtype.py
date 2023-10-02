from java.util import ArrayList


def get_some_list(is_java: bool) -> ArrayList | list:
    if is_java:
        return ArrayList()
    else:
        return [1, 2, 3]
