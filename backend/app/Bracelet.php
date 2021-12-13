<?php

namespace App;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Support\Facades\URL;

class Bracelet extends Model
{
    protected $fillable = ['uid', 'name', 'last_action'];

    public function scopeSearch($query, $value)
    {
        if($value && !empty($value)){
            return $query->where(function ($query) use ($value){
                $query->where('name', 'LIKE', '%'.$value.'%')
                    ->orWhere('uid', 'LIKE', '%'.$value.'%')
                    ->orWhere('last_action', 'LIKE', '%'.$value.'%');
            });
        }
    }

    public function logs(){
        return $this->hasMany(BraceletLog::class, 'bracelet_id', 'id');
    }

    public function getSignedUrlAttribute()
    {
        return URL::signedRoute(
            'bracelet.status',
            ['bracelet' => $this]
        );
    }

    public function getSignedUrlActivateAttribute()
    {
        return URL::signedRoute(
            'bracelet.activate',
            ['bracelet' => $this]
        );
    }
}
