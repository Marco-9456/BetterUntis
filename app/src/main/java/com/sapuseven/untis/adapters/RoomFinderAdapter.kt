package com.sapuseven.untis.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.sapuseven.untis.R

class RoomFinderAdapter(
		private val context: Context,
		private val onClickListener: RoomFinderClickListener,
		private val roomList: List<RoomFinderAdapterItem> = ArrayList()
) : RecyclerView.Adapter<RoomFinderAdapter.ViewHolder>() {

	private var currentHourIndex: Int = 0

	init {
		setHasStableIds(true)
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		val v = LayoutInflater.from(parent.context).inflate(R.layout.item_roomfinder, parent, false)
		v.setOnClickListener(onClickListener)
		return ViewHolder(v)
	}

	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		val room = roomList[position]
		room.hourIndex = currentHourIndex

		holder.tvName.text = room.name

		when {
			room.getState(currentHourIndex) == RoomFinderAdapterItem.STATE_OCCUPIED -> holder.tvDetails.text = context.resources.getString(R.string.room_desc_occupied)
			room.getState(currentHourIndex) >= RoomFinderAdapterItem.STATE_FREE -> holder.tvDetails.text = context.resources.getQuantityString(R.plurals.room_desc, room.getState(currentHourIndex), room.getState(currentHourIndex))
			else -> holder.tvDetails.text = "Loading data" //context.resources.getString(R.string.loading_data) // TODO: extract string
		}

		if (room.getState(currentHourIndex) >= RoomFinderAdapterItem.STATE_FREE && !room.loading) {
			holder.ivState.setImageResource(R.drawable.roomfinder_available)
			holder.ivState.visibility = View.VISIBLE
			holder.pbLoading.visibility = View.GONE
			holder.btnRoomExpired.visibility = if (room.isOutdated) View.VISIBLE else View.GONE
		} else if (room.getState(currentHourIndex) == RoomFinderAdapterItem.STATE_OCCUPIED && !room.loading) {
			holder.ivState.setImageResource(R.drawable.roomfinder_occupied)
			holder.ivState.visibility = View.VISIBLE
			holder.pbLoading.visibility = View.GONE
			holder.btnRoomExpired.visibility = if (room.isOutdated) View.VISIBLE else View.GONE
		} else {
			holder.ivState.visibility = View.GONE
			holder.pbLoading.visibility = View.VISIBLE
			holder.btnRoomExpired.visibility = View.GONE
		}

		holder.btnDelete.setOnClickListener { onClickListener.onDeleteClick(holder.adapterPosition) }

		holder.btnRoomExpired.setOnClickListener { onClickListener.onExpiredClick(holder.adapterPosition) }
	}

	override fun getItemCount(): Int {
		return roomList.size
	}

	override fun getItemId(position: Int): Long {
		return roomList[position].hashCode().toLong()
	}

	class ViewHolder(rootView: View) : RecyclerView.ViewHolder(rootView) {
		val tvName: TextView = rootView.findViewById(R.id.textview_roomfinder_name)
		val tvDetails: TextView = rootView.findViewById(R.id.textview_roomfinder_details)
		val ivState: AppCompatImageView = rootView.findViewById(R.id.imageview_roomfinder_state)
		val pbLoading: ProgressBar = rootView.findViewById(R.id.progressbar_roomfinder_loading)
		val btnDelete: ImageButton = rootView.findViewById(R.id.button_roomfinder_delete)
		val btnRoomExpired: ImageButton = rootView.findViewById(R.id.button_roomfinder_expired)
	}

	interface RoomFinderClickListener:View.OnClickListener {
		fun onDeleteClick(position: Int)
		fun onExpiredClick(position: Int)
	}
}
