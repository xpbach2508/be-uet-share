import matplotlib.pyplot as plt
import pandas as pd
import seaborn as sns

# Updated data with Path Length 6 values
data = {
    'Request': [50, 100, 150, 200],
    'Basic': [0.40, 0.26, 0.27, 0.28],
    'Linear': [0.40, 0.26, 0.28, 0.32],
    'Prophet': [0.31, 0.36, 0.36, 0.39],
    'Prune-prophet-25%': [0.33, 0.36, 0.34, 0.40],
    'Non-sharing': [1.0, 1.0, 1.0, 1.0]
}




# Convert data to DataFrame
df = pd.DataFrame(data)

# Set Seaborn style
sns.set_theme(style="whitegrid")

# Marker styles for each algorithm
markers = {
    'Basic': 's',  # Square
    'Linear': '^',  # Triangle
    'Prophet-25%': 'o',  # Circle
    'Prune-prophet-25%': 'D',  # Diamond
    'Non-sharing': 'X'  # Cross
}

# Plotting the line chart
plt.figure(figsize=(10, 6))
for algorithm, marker in markers.items():
    plt.plot(df['Request'], df[algorithm], label=algorithm, marker=marker, linestyle='-', markersize=8)

# Adding labels and title
font_size = 14  # Consistent font size
plt.xlabel('Total requests', labelpad=15, fontsize=font_size + 2, fontweight='bold')
plt.ylabel('Completion cost per request', labelpad=15, fontsize=font_size + 2, fontweight='bold')
plt.xticks(df['Request'])
plt.legend(title='Algorithm', fontsize=12)
plt.grid(axis='y', linestyle='--', linewidth=1)
plt.grid(axis='x', linestyle='--', linewidth=1)
plt.xticks(fontsize=font_size)
plt.yticks(fontsize=font_size)

# Show plot
plt.tight_layout()
plt.show()
