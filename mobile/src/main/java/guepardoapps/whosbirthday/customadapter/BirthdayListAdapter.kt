package guepardoapps.whosbirthday.customadapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.afollestad.materialdialogs.MaterialDialog
import com.plattysoft.leonids.ParticleSystem
import com.rey.material.widget.FloatingActionButton
import guepardoapps.whosbirthday.R
import guepardoapps.whosbirthday.activities.ActivityEdit
import guepardoapps.whosbirthday.common.Constants
import guepardoapps.whosbirthday.controller.BirthdayController
import guepardoapps.whosbirthday.controller.NavigationController
import guepardoapps.whosbirthday.database.birthday.DbBirthday
import guepardoapps.whosbirthday.extensions.common.integerFormat
import guepardoapps.whosbirthday.model.Birthday

internal class BirthdayListAdapter(private val context: Context, private val list: Array<Birthday>) : BaseAdapter() {

    private val navigationController: NavigationController = NavigationController(context)
    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    private class Holder {
        lateinit var name: TextView
        lateinit var group: TextView
        lateinit var date: TextView
        lateinit var age: TextView

        lateinit var remindMe: CheckBox

        lateinit var edit: FloatingActionButton
        lateinit var delete: FloatingActionButton
    }

    override fun getItem(position: Int): Any {
        return list[position]
    }

    override fun getItemId(position: Int): Long {
        return list[position].id
    }

    override fun getCount(): Int {
        return list.size
    }

    @SuppressLint("SetTextI18n", "ViewHolder", "InflateParams")
    override fun getView(index: Int, convertView: View?, parentView: ViewGroup?): View {
        val rowView: View = inflater.inflate(R.layout.list_item, null)

        val birthday = list[index]

        val holder = Holder()
        holder.name = rowView.findViewById(R.id.birthday_item_name)
        holder.group = rowView.findViewById(R.id.birthday_item_group)
        holder.date = rowView.findViewById(R.id.birthday_item_date)
        holder.age = rowView.findViewById(R.id.birthday_item_age)

        holder.remindMe = rowView.findViewById(R.id.birthday_remind_me)

        holder.edit = rowView.findViewById(R.id.btnEdit)
        holder.delete = rowView.findViewById(R.id.btnDelete)

        holder.name.text = birthday.name
        holder.group.text = birthday.group
        holder.date.text = "${birthday.day.integerFormat(2)}.${birthday.month.integerFormat(2)}.${birthday.year.integerFormat(4)}"
        holder.age.text = "${birthday.getAge()}"

        holder.remindMe.isChecked = birthday.remindMe
        holder.remindMe.setOnCheckedChangeListener { _, isChecked ->
            birthday.remindMe = isChecked
            DbBirthday(context).update(birthday)
            BirthdayController().checkForBirthday(context)
        }

        holder.edit.setOnClickListener {
            val bundle = Bundle()
            bundle.putLong(Constants.bundleDataId, birthday.id)
            navigationController.navigateWithData(ActivityEdit::class.java, bundle, false)
        }

        holder.delete.setOnClickListener {
            MaterialDialog(context).show {
                title(text = "Delete")
                message(text = "Delete ${birthday.name}?")
                positiveButton(text = "Yes") { _ -> DbBirthday(context).delete(birthday.id.toInt()) }
                negativeButton(text = "No")
            }
        }

        if (birthday.hasBirthday()) {
            rowView.setBackgroundColor(context.getColor(R.color.colorPrimaryDark))
            ParticleSystem(
                    context as Activity,
                    150,
                    R.drawable.particle,
                    750)
                    .setSpeedRange(0.2f, 0.5f)
                    .oneShot(rowView, 150)
        }

        return rowView
    }
}