<?php

namespace App;

use Illuminate\Database\Eloquent\Model;

class BraceletLog extends Model
{
    protected $fillable = ['bracelet_id', 'action', 'date_time'];

    protected $dates = ['date_time'];

    public function bracelet()
    {
        return $this->belongsTo(Bracelet::class);
    }
}
