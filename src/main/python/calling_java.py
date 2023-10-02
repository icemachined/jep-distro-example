from java.util import HashMap
from java.util import ArrayList as AL
from java.util.concurrent.atomic import AtomicInteger

a = AL()
a.add("One")
a += ["Two"]
a = a + ["Three"]
a = [f"{x} icecream" for x in a]
print(f"a={a}")
print(f"a[1]={len(a)}") # __len__ added to java.util.Collection
print(f"Contains one icecream: {'One icecream' in a}") # __contains__ added to java.util.Collection
print(f"a[1]={a[1]}") # __getitem__ added by java.util.List
a[1] = 2 # __setitem__ added by java.util.List
print(f"a={a}")

m = HashMap()
m.put("icecreams", a)
m["cars"] = "xpeng"
print(f"m={m}")
print(f"Cars are in m: {'cars' in m}")

atomicInt = AtomicInteger(9)
print(f"One third of atomic: {atomicInt/3}")  # Added by java.util.Number
