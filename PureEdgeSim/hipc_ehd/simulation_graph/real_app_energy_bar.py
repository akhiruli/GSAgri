import numpy as np
import matplotlib.pyplot as plt

barWidth = 0.15
fig = plt.subplots(figsize =(8, 6))

X=['GSAgri', 'SCOPE', 'RG', 'OCO']

Y=[0.7234, 4.378, 5.11, 1.675]

colors=['g', 'b', 'orange', 'm']

plt.bar(X, Y, color=colors, width=0.5)

plt.xlabel("Strategies", fontsize = 15)
plt.ylabel("Energy Consumption at UD (Wh)", fontsize = 15)
plt.xticks(fontsize=13)
plt.savefig('real_app_energy_bar.png', bbox_inches='tight')
plt.show()
