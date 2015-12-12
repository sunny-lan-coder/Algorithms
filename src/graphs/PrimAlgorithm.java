package graphs;

public class PrimAlgorithm {

	public static final int NO_EDGE = Integer.MAX_VALUE;

	private int[][] adjMatrix;
	private boolean[] tree;
	private boolean[] graph;

	public PrimAlgorithm(int[][] adjMatrix) {
		this.adjMatrix = adjMatrix;
		graph = new boolean[adjMatrix.length];
		tree = new boolean[adjMatrix.length];
		for (int i = 1; i < graph.length; i++)
			graph[i] = true;
		tree[0] = true;
	}

	public boolean[][] findMST() {
		boolean[][] adjMatrixR = new boolean[adjMatrix.length][adjMatrix[0].length];
		int minJ, minI;
		while (true) {
			minJ = 0;
			minI = -1;
			for (int i = 0; i < tree.length; i++) {
				if (tree[i]) {
					for (int j = 0; j < adjMatrix[i].length; j++) {
						if (graph[j]) {
							if (minI == -1) {
								minI = i;
								minJ = j;
								continue;
							}
							if (adjMatrix[i][j] < adjMatrix[minI][minJ]) {
								minI = i;
								minJ = j;
							}
						}
					}
				}
			}
			if (minI == -1) {
				break;
			}
			tree[minJ] = true;
			graph[minJ] = false;

			adjMatrixR[minI][minJ] = true;
		}
		return adjMatrixR;
	}

	public static void main(String[] args) {
		int[][] adjMatrix = { { NO_EDGE, 1, NO_EDGE, 10, 3, NO_EDGE },
				{ 1, NO_EDGE, 2, 3, NO_EDGE, NO_EDGE },
				{ NO_EDGE, 2, NO_EDGE, 4, NO_EDGE, 5 },
				{ 10, 3, 4, NO_EDGE, 4, 1 },
				{ 3, NO_EDGE, NO_EDGE, 4, NO_EDGE, NO_EDGE },
				{ NO_EDGE, NO_EDGE, 5, 1, NO_EDGE, NO_EDGE } };
		PrimAlgorithm alg = new PrimAlgorithm(adjMatrix);
		boolean[][] adjMatrixR = alg.findMST();
		for (boolean[] row : adjMatrixR) {
			for (boolean val : row) {
				if (val) {
					System.out.print("1 ");
				} else {
					System.out.print("0 ");
				}
			}
			System.out.println();
		}

	}

}
