{{-- SEARCH --}}
<div class="card-tools">
    <form method="GET">
        <div class="input-group input-group-sm" style="width: 150px;">
            <input type="text" name="search" value="{{(isset($_GET['search'])) ? $_GET['search'] : ''}}" class="form-control float-right" placeholder="@lang('Search')">

            <div class="input-group-append">
                <button type="submit" class="btn btn-default">
                    <i class="fas fa-search"></i>
                </button>
            </div>
        </div>
    </form>
</div>
{{-- --}}
