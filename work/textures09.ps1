Add-Type -AssemblyName System.Drawing
$base=Join-Path $PSScriptRoot '..\src\main\resources\assets\lastbreath\textures'
$generated='C:\Users\Vlad\.codex\generated_images\01a14e41-fd25-7831-a996-501ab25ed225'
function Resize-Tile($source,$output,$size) {
 $im=[Drawing.Image]::FromFile($source);$b=[Drawing.Bitmap]::new($size,$size);$g=[Drawing.Graphics]::FromImage($b)
 $g.InterpolationMode=[Drawing.Drawing2D.InterpolationMode]::NearestNeighbor;$g.PixelOffsetMode=[Drawing.Drawing2D.PixelOffsetMode]::Half
 $g.DrawImage($im,[Drawing.Rectangle]::new(0,0,$size,$size),0,0,$im.Width,$im.Height,[Drawing.GraphicsUnit]::Pixel)
 $g.Dispose();$im.Dispose();$b.Save($output,[Drawing.Imaging.ImageFormat]::Png);$b.Dispose()
}
Resize-Tile (Join-Path $generated 'exec-eb7d0306-e4d6-4e5d-9356-06f658bc407c.png') (Join-Path $base 'block\ritual_table_top.png') 16
# Existing altar remains the same block. Mechanically reduce its existing top ornament.
Resize-Tile (Join-Path $base 'block\return_altar_top.png') (Join-Path $base 'block\return_altar_top.png') 16
Resize-Tile (Join-Path $base 'block\return_altar_side.png') (Join-Path $base 'block\return_altar_side.png') 16
function Soul-Sheet($source,$name) {
 $im=[Drawing.Image]::FromFile($source);$cell=[int]($im.Width/2)
 foreach($overlay in @($false,$true)) {
  $b=[Drawing.Bitmap]::new(32,128);$g=[Drawing.Graphics]::FromImage($b);$g.Clear([Drawing.Color]::Transparent)
  $g.InterpolationMode=[Drawing.Drawing2D.InterpolationMode]::NearestNeighbor;$g.PixelOffsetMode=[Drawing.Drawing2D.PixelOffsetMode]::Half
  for($i=0;$i -lt 4;$i++) {
   $sx=($i%2)*$cell;$sy=[int][Math]::Floor($i/2)*$cell
   if($overlay){$crop=0;$extent=$cell}else{$crop=[int]($cell*.12);$extent=$cell-2*$crop}
   $g.DrawImage($im,[Drawing.Rectangle]::new(0,$i*32,32,32),$sx+$crop,$sy+$crop,$extent,$extent,[Drawing.GraphicsUnit]::Pixel)
  }
  $g.Dispose();$suffix=if($overlay){'_overlay'}else{''};$b.Save((Join-Path $base "item\$name$suffix.png"),[Drawing.Imaging.ImageFormat]::Png);$b.Dispose()
 }
 $im.Dispose()
}
Soul-Sheet (Join-Path $generated 'exec-fb21e312-9117-49fd-b5d5-e9c20639db97.png') 'red_soul'
Soul-Sheet (Join-Path $generated 'exec-ed1716d3-fae5-4b27-a379-87b2c24792af.png') 'purple_soul'
Soul-Sheet (Join-Path $generated 'exec-36113006-3ebd-497d-99f8-492a4f4da482.png') 'mob_soul'
