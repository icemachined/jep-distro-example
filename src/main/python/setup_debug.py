def enable_debugger(pydevdegg, pydevdhost, pydevdport):
    try:
        import sys
        sys.path.append(pydevdegg)
        import pydevd_pycharm
        pydevd_pycharm.settrace(
            pydevdhost,
            port=pydevdport,
            stdoutToServer=True,
            stderrToServer=True)

    except Exception as e:
        raise RuntimeError(
            "Failed to attach debugger: %s" %
            e)
