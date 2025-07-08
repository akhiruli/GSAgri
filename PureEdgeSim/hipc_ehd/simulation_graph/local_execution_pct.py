import numpy as np
import matplotlib.pyplot as plt

barWidth = 0.15
fig = plt.subplots(figsize =(8, 6))

X=['GSAgri', 'SCOPE', 'RG', 'OCO']

Y=[4.4, 35.4, 33.9, 27.3]

colors=['g', 'b', 'orange', 'm']

plt.bar(X, Y, color=colors, width=0.5)

plt.xlabel("Strategies", fontsize = 15)
plt.ylabel("% of tasks executed locally", fontsize = 15)
plt.xticks(fontsize=13)
plt.savefig('local_execution_pct.png', bbox_inches='tight')
plt.show()
