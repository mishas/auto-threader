package autothreader;

import java.util.HashSet;
import java.util.Set;

public class DirectedGraph<T> {
	
	private Set<T> vertices = new HashSet<T>();
	private Set<DirectedEdge<T>> edges = new HashSet<DirectedEdge<T>>();
	
	public void addVertex(T v){
		
		this.vertices.add(v);
	}
	
	public void addEdge(T v1,T v2){
		this.edges.add(new DirectedEdge<T>(v1,v2));
	}
	
	public void removeVertex(T v){
		for(DirectedEdge<T> e:this.edges)
			if(e.first == v || e.second == v)
				this.edges.remove(e);
		this.vertices.remove(v);
	}
	
	public void removeEdge(T v1,T v2){
		for(DirectedEdge<T> e:this.edges)
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
		for(DirectedEdge<T> e:edges)
			sb.append(e.getFirst().toString()+"->"+e.getSecond().toString()+",\n");
		return sb.toString();
	}
	
	public class DirectedEdge<T>{
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
			DirectedEdge<?> e = (DirectedEdge<?>) obj;
			return this.first.equals(e.getFirst())&&this.second.equals(e.getSecond());
		}
		
		@Override
		public int hashCode() {
			return this.first.hashCode()+this.second.hashCode();
		}
	}
}
