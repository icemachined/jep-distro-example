from java.util import HashMap
from java.util import ArrayList as AL

a = AL()
a.add("abc")
a += ["def"]
print(a)

m = HashMap()
m.put("listkey", a)
m["otherkey"] = "xyz"
print(m)