package com.example.proyecto.SupportAndTips

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto.Language_Theme.BaseActivity
import com.example.proyecto.R

data class FAQItem(val questionResId: Int, val answerResId: Int)

class SupportAndTipsActivity : BaseActivity() {

    private lateinit var faqRecyclerView: RecyclerView
    private lateinit var faqAdapter: FAQAdapter

    private val faqList = listOf(
        FAQItem(R.string.faq_question_1, R.string.faq_answer_1),
        FAQItem(R.string.faq_question_2, R.string.faq_answer_2),
        FAQItem(R.string.faq_question_3, R.string.faq_answer_3),
        FAQItem(R.string.faq_question_4, R.string.faq_answer_4),
        FAQItem(R.string.faq_question_5, R.string.faq_answer_5),
        FAQItem(R.string.faq_question_6, R.string.faq_answer_6),
        FAQItem(R.string.faq_question_7, R.string.faq_answer_7),
        FAQItem(R.string.faq_question_8, R.string.faq_answer_8),
        FAQItem(R.string.faq_question_9, R.string.faq_answer_9),
        FAQItem(R.string.faq_question_10, R.string.faq_answer_10),
        FAQItem(R.string.faq_question_11, R.string.faq_answer_11),
        FAQItem(R.string.faq_question_12, R.string.faq_answer_12),
        FAQItem(R.string.faq_question_13, R.string.faq_answer_13),
        FAQItem(R.string.faq_question_14, R.string.faq_answer_14),
        FAQItem(R.string.faq_question_15, R.string.faq_answer_15)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_support_and_tips)

        faqRecyclerView = findViewById(R.id.rvFaqList)
        faqRecyclerView.layoutManager = LinearLayoutManager(this)
        faqAdapter = FAQAdapter(faqList)
        faqRecyclerView.adapter = faqAdapter
    }

    class FAQAdapter(private val items: List<FAQItem>) : RecyclerView.Adapter<FAQAdapter.FAQViewHolder>() {

        private var expandedPosition = -1
        private val animatedPositions = mutableSetOf<Int>()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FAQViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_faq, parent, false)
            return FAQViewHolder(view)
        }

        override fun onBindViewHolder(holder: FAQViewHolder, position: Int) {
            val isExpanded = position == expandedPosition
            val item = items[position]
            holder.bind(item, isExpanded)

            // Animación en cascada: solo la primera vez
            if (!animatedPositions.contains(position)) {
                val context = holder.itemView.context
                val animation = AnimationUtils.loadAnimation(context, R.anim.slide_in_left)
                animation.startOffset = (position * 100).toLong() // cascada: 0ms, 100ms, 200ms...
                holder.itemView.startAnimation(animation)
                animatedPositions.add(position)
            }

            holder.itemView.setOnClickListener {
                val currentPosition = holder.adapterPosition
                if (currentPosition == RecyclerView.NO_POSITION) return@setOnClickListener

                if (expandedPosition == currentPosition) {
                    expandedPosition = -1
                    notifyItemChanged(currentPosition)
                } else {
                    val oldExpanded = expandedPosition
                    expandedPosition = currentPosition
                    notifyItemChanged(oldExpanded)
                    notifyItemChanged(currentPosition)
                }
            }
        }

        override fun getItemCount(): Int = items.size

        inner class FAQViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val tvQuestion: TextView = itemView.findViewById(R.id.tvQuestion)
            private val tvAnswer: TextView = itemView.findViewById(R.id.tvAnswer)

            fun bind(item: FAQItem, isExpanded: Boolean) {
                val context = itemView.context
                tvQuestion.text = context.getString(item.questionResId)
                tvAnswer.text = context.getString(item.answerResId)
                tvAnswer.visibility = if (isExpanded) View.VISIBLE else View.GONE
            }
        }
    }
}
