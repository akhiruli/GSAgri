import numpy as np
import matplotlib.pyplot as plt

barWidth = 0.15
fig = plt.subplots(figsize =(8, 6))

X=['GSAgri', 'SCOPE', 'RG', 'OCO']

Y=[56.42, 71.59, 67.13, 69.602]

colors=['g', 'b', 'orange', 'm']

plt.bar(X, Y, color=colors, width=0.5)

plt.xlabel("Strategies", fontsize = 15)
plt.ylabel("Average Latency (Secs)", fontsize = 15)
plt.xticks(fontsize=13)
plt.savefig('real_app_latency_bar.png', bbox_inches='tight')
plt.show()
