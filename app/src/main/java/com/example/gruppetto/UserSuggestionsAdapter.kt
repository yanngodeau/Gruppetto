package com.example.gruppetto

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mancj.materialsearchbar.adapter.SuggestionsAdapter


class UserSuggestionsAdapter(inflater: LayoutInflater) :
    SuggestionsAdapter<User, UserSuggestionsAdapter.SuggestionHolder>(inflater) {

    override fun getSingleViewHeight(): Int {
        return 80
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SuggestionHolder {
        val view: View =
            layoutInflater.inflate(R.layout.item_user_suggestion, parent, false)
        return SuggestionHolder(view)
    }

    override fun onBindSuggestionHolder(suggestion: User, holder: SuggestionHolder, position: Int) {
        holder.title.text = suggestion.name
        holder.subtitle.text = suggestion.mail
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence): FilterResults {
                val results = FilterResults()
                val term = constraint.toString()
                if (term.isEmpty()) suggestions = suggestions_clone else {
                    suggestions = ArrayList<User>()
                    for (item in suggestions_clone) if (item.name.contains(term)) {
                        suggestions.add(item)
                    }
                }
                results.values = suggestions
                return results
            }

            override fun publishResults(constraint: CharSequence, results: FilterResults) {
                suggestions = results.values as ArrayList<User?>
                notifyDataSetChanged()
            }
        }
    }

    class SuggestionHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var title: TextView
        var subtitle: TextView
        protected var image: ImageView? = null

        init {
            title = itemView.findViewById<View>(R.id.user_name) as TextView
            subtitle = itemView.findViewById<View>(R.id.user_name) as TextView
        }
    }
}
