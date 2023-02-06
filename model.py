import torch

class model:
    def __init__(self,weights_path) -> None:
        self.model = torch.hub.load('WongKinYiu/yolov7', 'custom', weights_path, force_reload = False, trust_repo=True)
    
    def get_results(self,images):
        results = self.model(images)
        result_table = results.pandas().xyxy[0]
        
        confidence = 0

        xmin_bbox = -1
        ymin_bbox = -1
        xmax_bbox = -1
        ymax_bbox = -1
        name = -1

        for _, row in result_table.iterrows():
            if (row["confidence"] > confidence):
                confidence = row["confidence"]
                xmin_bbox = int(row["xmin"])
                ymin_bbox = int(row["ymin"])
                xmax_bbox = int(row["xmax"])
                ymax_bbox = int(row["ymax"])
                name = row["name"]
        
        results_array = [[xmin_bbox, ymin_bbox, xmax_bbox, ymax_bbox], name] 
        return results_array