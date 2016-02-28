package util;

import java.util.HashSet;
import java.util.Set;

public class DirectedGraph<T> {
	
	private Set<T> vertices = new HashSet<T>();
	private Set<DirectedEdge> edges = new HashSet<DirectedEdge>();
	
	private Set<DirectedEdge> getOutgoingEdges(T v){
		Set<DirectedEdge> ret = new HashSet<DirectedEdge>();
		for(DirectedEdge e : edges){
			if(e.getFirst()==v)
				ret.add(e);
		}
		return ret;
	}
	
	//BFS
	public boolean isReachable(T v1,T v2){
		if(v1 == v2)
			return true;
		Set<DirectedEdge> edgesToVisit = new HashSet<DirectedEdge>();
		Set<T> verticesToVisit ;
		edgesToVisit.addAll(getOutgoingEdges(v1));
		
		while(edgesToVisit.size()>0){
			verticesToVisit = new HashSet<T>();
			for(DirectedEdge e : edgesToVisit){
				verticesToVisit.add(e.getSecond());
			}
			if(verticesToVisit.size()>0)
				return false;
			edgesToVisit = new HashSet<DirectedEdge>();
			for(T v:verticesToVisit){
				if(v == v2)
					return true;
				edgesToVisit.addAll(getOutgoingEdges(v));
			}
		}
		return false;
	}
	
	public DirectedGraph<T> getCopy(){
		DirectedGraph<T> copy = new DirectedGraph<T>();
		for(T v:vertices)
			copy.addVertex(v);
		for(DirectedEdge e : edges)
			copy.addEdge(e.getFirst(), e.getSecond());
		return copy;
	}
	
	public void addVertex(T v){
		
		this.vertices.add(v);
	}
	
	public void addEdge(T v1,T v2){
		addVertex(v1);
		addVertex(v2);
		this.edges.add(new DirectedEdge(v1,v2));
	}
	
	public void removeVertex(T v){
		removeAllEdges(v);
		this.vertices.remove(v);
	}
	public void removeAllEdges(T v){
		HashSet<DirectedEdge> toRemove = new HashSet<>();
		for(DirectedEdge e : this.edges)
			if(e.first == v || e.second == v)
				toRemove.add(e);
		this.edges.removeAll(toRemove);
	}
	
	public void removeEdge(T v1,T v2){
		for(DirectedEdge e : this.edges)
			if(e.first == v1 && e.second == v2){
				this.edges.remove(e);
				return;
			}
	}
	
	public boolean containsVertes(T v){
		return this.vertices.contains(v);
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder("Graph:\n");
		for(DirectedEdge e : edges)
			sb.append(e.getFirst().toString()+"->"+e.getSecond().toString()+",\n");
		return sb.toString();
	}
	
	public class DirectedEdge{
		private T first;
		private T second;
		
		public DirectedEdge(T f,T s){
			this.first = f;
			this.second = s;
		}
		
		public T getFirst(){
			return first;
		}
		
		public T getSecond(){
			return second;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (obj instanceof DirectedGraph<?>.DirectedEdge) {
				DirectedGraph<?>.DirectedEdge e = (DirectedGraph<?>.DirectedEdge) obj;
				return this.first.equals(e.getFirst()) && this.second.equals(e.getSecond());
			}
			return false;
		}
		
		@Override
		public int hashCode() {
			return this.first.hashCode()+this.second.hashCode();
		}
	}
	
	public static <T> DirectedGraph<T> join(DirectedGraph<T> g1,DirectedGraph<T> g2){
		DirectedGraph<T> g3 = g1.getCopy();
		g3.vertices.addAll(g2.vertices);
		g3.edges.addAll(g2.edges);
		return g3;
	}
}
