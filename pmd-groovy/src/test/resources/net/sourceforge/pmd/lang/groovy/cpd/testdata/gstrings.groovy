// https://github.com/pmd/pmd/issues/7100
def a = "$i"
f("x-$i", "y")
def b = "x $i y"
def c = "${i}"
def d = "$i.name"
def e = """multi $i"""
