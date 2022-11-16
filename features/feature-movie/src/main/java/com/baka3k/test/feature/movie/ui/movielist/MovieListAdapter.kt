package com.baka3k.test.feature.movie.ui.movielist

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.baka3k.architecture.core.ui.view.ShimmerLoading
import com.baka3k.core.model.Movie
import com.baka3k.test.feature.movie.R

class MovieListAdapter(
    data: List<MovieState>,
    private val onItemClicked: (MovieState) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val movies: MutableList<MovieState> = mutableListOf()

    init {
        movies.addAll(data)
    }

    fun showLoadMore() {
        movies.add(MovieState.Loading)
        movies.add(MovieState.Loading)
        movies.add(MovieState.Loading)
    }

    fun fillData(data: List<MovieState>) {
        movies.removeIf { it is MovieState.Loading }
        movies.addAll(data)
        notifyDataSetChanged()
    }

    class MovieViewHolder(
        itemView: View,
        onItemClicked: (Int) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.imageView)
        private val title: TextView = itemView.findViewById(R.id.title)

        init {
            imageView.setOnClickListener {
                onItemClicked(absoluteAdapterPosition)
            }
        }

        fun onBindViewHolder(movie: Movie) {
            title.text = movie.title
            val urlImage = movie.posterPath
//            val urlImage = "https://www.geeksforgeeks.org/wp-content/uploads/gfg_200X200-1.png"
            Log.d("MovieListAdapter", "urlImage $urlImage")
            imageView.load(urlImage) {
                crossfade(true)
                placeholder(androidx.appcompat.R.drawable.abc_dialog_material_background)
//                transformations(CircleCropTransformation())
//                crossfade(true)
//                scale(Scale.FIT)
//                placeholder(com.baka3k.architecture.core.ui.R.drawable.ic_placeholder_default)
//                size(width = 250, height = 300)s
            }
        }
    }

    class ShimmerHolder(
        itemView: View,
    ) : RecyclerView.ViewHolder(itemView) {
    }

    override fun getItemCount(): Int {
        return movies.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 1) {
            val view: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_popular, parent, false)
            MovieViewHolder(view) {
                onItemClicked(movies[it])
            }
        } else {
//            val view: View = LayoutInflater.from(parent.context)
//                .inflate(R.layout.composeview, parent, false)
            ShimmerHolder(ShimmerLoading(parent.context))
        }
    }

    override fun getItemViewType(position: Int): Int {
        val movieState = movies[position]
        return if (movieState is MovieState.Data) {
            1
        } else {
            0
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder.itemViewType == 1) {
            val movieState = movies[position]
            if (movieState is MovieState.Data) {
                (holder as MovieViewHolder).onBindViewHolder(movie = movieState.movie)
            }
        }
    }
}