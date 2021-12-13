<div class="form-row">
    <div class="form-group col-md-6">
        <label for="name">@lang('Name') <span style="color: red;">*</span></label>
        <input type="text" class="form-control @error('name') is-invalid @enderror" id="name" name="name" @if(old('name')) value="{{ old('name') }}" @elseif(isset($bracelet)) value="{{$bracelet->name}}" @endif>
        @error('name')
            <span class="error invalid-feedback">{{ $message }}</span>
        @enderror
    </div>
    <div class="form-group col-md-6">
        <label for="email">@lang('UID') <span style="color: red;">*</span></label>
        <input type="text" class="form-control @error('uid') is-invalid @enderror" id="uid" name="uid" @if(old('uid')) value="{{ old('uid') }}" @elseif(isset($bracelet)) value="{{$bracelet->uid}}" @endif>
        @error('uid')
            <span class="error invalid-feedback">{{ $message }}</span>
        @enderror
    </div>
    <div class="form-group col-md-6">
        <label for="instance_type">@lang('Status') <span style="color: red;">*</span></label>
        <select id="state" name="state" class="form-control @error('state') is-invalid @enderror">
            <option value="disabled"  @if((old('state') && old('state') == 'disabled') || (isset($bracelet) && $bracelet->last_action == 'disabled')) selected @endif>disabled</option>
            <option value="out"  @if((old('state') && old('state') == 'out') || (isset($bracelet) && $bracelet->last_action == 'out')) selected @endif>outside</option>
            <option value="in" @if((old('state') && old('state') == 'in') || (isset($bracelet) && $bracelet->last_action == 'in')) selected @endif>inside</option>
        </select>
        @error('state')
            <span class="error invalid-feedback">{{ $message }}</span>
        @enderror
    </div>
</div>
